package main

import (
	"flag"
	"html/template"
	"io/ioutil"
	"log"
	"math/rand"
	"net/http"
	"net/url"
	"path/filepath"
	"strconv"
	"strings"

	"gopkg.in/redis.v3"

	"github.com/julienschmidt/httprouter"
	"github.com/pilu/go-base62"
)

var host = flag.String("host", "https://fn.lc/", "the default web host")
var listen = flag.String("listen", ":8080", "the address to listen on")

var endings = []string{"mkv", "mp4", "dll", "exe", "so", "dmg", "msi", "jar", "bat", "cmd", "py", "sh", "pdf"}

var templates = template.Must(template.ParseFiles("public/index.html"))

var wordFiles []wordFile
var wordFilesMap map[string]wordFile

type wordFile struct {
	Name  string
	Words []string
}

type templateConfig struct {
	Index, NotFound, Shortened bool
	WordFiles                  []wordFile
	From, To                   string
}

func loadWordFiles() error {
	wordFilesMap = make(map[string]wordFile)
	matches, err := filepath.Glob("words/*")
	if err != nil {
		return err
	}
	for _, match := range matches {
		f := wordFile{
			Name: filepath.Base(match),
		}
		body, err := ioutil.ReadFile(match)
		if err != nil {
			return err
		}
		for _, line := range strings.Split(string(body), "\n") {
			if len(line) == 0 || line[0:1] == "#" {
				continue
			}
			for _, word := range strings.Split(line, ",") {
				word = strings.TrimSpace(word)
				f.Words = append(f.Words, word)
			}
		}
		wordFiles = append(wordFiles, f)
		wordFilesMap[f.Name] = f
	}
	return nil
}

func Index(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	templates.ExecuteTemplate(w, "index.html", templateConfig{Index: true, WordFiles: wordFiles})
}
func Get(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	switch ps.ByName("file") {
	case "new", "short":
		Index(w, r, ps)
		return
	}
	url, err := client.Get("sketchy:url:" + ps.ByName("file")).Result()
	if err != nil && err != redis.Nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	if err == redis.Nil {
		w.WriteHeader(http.StatusNotFound)
		templates.ExecuteTemplate(w, "index.html", templateConfig{NotFound: true})
	} else {
		http.Redirect(w, r, url, http.StatusMovedPermanently)
	}
}

func New(w http.ResponseWriter, r *http.Request, ps httprouter.Params) {
	r.ParseForm()
	wordCount, err := strconv.Atoi(r.Form.Get("words"))
	if err != nil || wordCount > 100 || len(r.Form.Get("normal")) > 0 || wordCount <= 0 {
		wordCount = 1
	}
	lists := r.Form["lists"]
	id, err := client.Incr("sketchy:latestid").Result()
	if err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}
	encID := base62.Encode(int(id))

	codeI := rand.Intn(wordCount)

	var words []string
	for i := 0; i < wordCount; i++ {
		if i == codeI {
			words = append(words, encID)
			continue
		}
		if len(lists) == 0 {
			continue
		}
		list := lists[rand.Intn(len(lists))]
		wordFile, ok := wordFilesMap[list]
		if !ok {
			continue
		}
		words = append(words, wordFile.Words[rand.Intn(len(wordFile.Words))])
	}

	for i, word := range words {
		switch rand.Intn(3) {
		case 0:
			words[i] = strings.ToLower(word)
		case 1:
			words[1] = strings.ToUpper(word)
		}
	}

	ext := "." + endings[rand.Intn(len(endings))]

	newURL := strings.Replace(strings.Join(words, "_"), " ", "_", -1) + ext

	parsed, err := url.Parse(r.Form.Get("url"))
	if err != nil {
		http.Error(w, "failed to parse URL", http.StatusBadRequest)
		return
	}
	if len(parsed.Scheme) == 0 {
		parsed.Scheme = "http"
	}

	if _, err := client.Set("sketchy:url:"+newURL, parsed.String(), 0).Result(); err != nil {
		http.Error(w, err.Error(), http.StatusInternalServerError)
		return
	}

	templates.ExecuteTemplate(w, "index.html", templateConfig{
		Shortened: true,
		From:      parsed.String(),
		To:        *host + newURL,
	})
}

var client *redis.Client

func main() {
	flag.Parse()

	if err := loadWordFiles(); err != nil {
		log.Fatal(err)
	}
	client = redis.NewClient(&redis.Options{
		Addr:     "localhost:6379",
		Password: "", // no password set
		DB:       0,  // use default DB
	})

	router := httprouter.New()
	router.GET("/", Index)
	router.POST("/new", New)
	router.GET("/:file", Get)

	log.Printf("Listening on %s...", *listen)
	log.Fatal(http.ListenAndServe(*listen, router))
}
