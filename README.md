# Sketchy URL Shortener

http://fn.lc/short

## Build & Run

```sh
$ cd Sketchy_URL_Shortener
$ ./sbt
> container:start
> browse
```

If `browse` doesn't launch your browser, manually open [http://localhost:8080/](http://localhost:8080/) in your browser.

## Word Lists
`nsa_watchlist.txt` is the keywords the NSA looks for. [Source](http://www.businessinsider.com/nsa-prism-keywords-for-domestic-spying-2013-6)

`nfl_dirty_words.txt` is the list of words banned from being put on jerseys. [Source](http://www.infochimps.com/datasets/list-of-dirty-obscene-banned-and-otherwise-unacceptable-words)

`linux_commands.txt` is a list of Linux commands available on my system. Obtained by running `compgen -c` in bash.

`cracklib-small` is a dictionary file found on my Arch Linux system at `/usr/share/dict/`. Not sure what it's from.

`pornstars.txt` is a generated list from [Wikipedia](https://en.wikipedia.org/wiki/List_of_pornographic_actresses_by_decade)
