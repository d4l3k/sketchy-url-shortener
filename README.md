# Sketchy URL Shortener

https://fn.lc/short

## Build & Run

```sh
$ go get -u github.com/d4l3k/sketchy-url-shortener
$ cd $GOPATH/src/github.com/d4l3k/sketch-url-shortener
$ sketchy-url-shortener -host="https://fn.lc/"
```

## Word Lists
`nsa_watchlist.txt` is the keywords the NSA looks for. [Source](http://www.businessinsider.com/nsa-prism-keywords-for-domestic-spying-2013-6)

`nfl_dirty_words.txt` is the list of words banned from being put on jerseys. [Source](http://www.infochimps.com/datasets/list-of-dirty-obscene-banned-and-otherwise-unacceptable-words)

`linux_commands.txt` is a list of Linux commands available on my system. Obtained by running `compgen -c` in bash.

`cracklib-small` is a dictionary file found on my Arch Linux system at `/usr/share/dict/`. Not sure what it's from.

`pornstars.txt` is a generated list from [Wikipedia](https://en.wikipedia.org/wiki/List_of_pornographic_actresses_by_decade)
