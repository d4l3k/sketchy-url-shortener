# Sketchy URL Shortener

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
