# winkler
[![Clojars Project](https://img.shields.io/clojars/v/imaginathansoft/winkler.svg)](https://clojars.org/imaginathansoft/winkler)

A moderately paranoid clojurescript library for generating more entropy. You may need a cryptographically secure RNG but who knows if it's enough.  This is just some extra salt.

[Overkill demo using dice](https://winkler-demo.netlify.app/)

## Installation

With Leinengen/Boot/shadow-cljs:
```
[imaginathansoft/winkler "0.3.0"]
```

With deps.edn:
```
imaginathansoft/winkler {:mvn/version "0.3.0"}
```

To build from source, run in the terminal:

```
git clone https://github.com/ndonolli/winkler.git
```

## Usage

Require the core namespace to use `generate` which will produce a lazy-seq of randomized integers. The sequence will produce until the total bits of entropy is greater than the provided argument amount.

```clojure
(require '[winkler.core :refer [generate]])
```

Generate random integers with at least 100 bits of combined entropy
```clojure
(generate {:entropy 100}) ;; => (1134 -419 16631 -2872 ...)
```
Without any arguments, `generate` will produce infinitely. So take precautions:
```clojure
(take 3 (generate)) ;; => (5081 -1092 -4678)
```

Although lazy, each `take` does require running timed computations in order to calculate entropy values. The default time spent on each calculation loop, the limit of bits to harvest, and other options are implicitly called with the generate function.  You can, however, override these details in the `opts` parameter:
- `:entropy` - takes from the sequence until the combined entropy is at least the given amount (default nil).
- `:max-bits` - max entropy value allowed per generation (default 4).
- `:work-min` - minimum time period (in ms) per each loop of operation crunching (default 1).
- `:buffer` - channel buffer size when utilizing `generate-async`, explained below. (default 10).

`generate` should cover most use-cases, and its lazy evaluation allows you to be flexible on when each potentially costly bit generation step is realized.  For cases where more entropy or time is needed, the core namespace provides async methods to help with blocking.

One option is to use `generate-promise`, which you can provide a callback for. For example, here we print the random interger sequence for a large combined entropy amount (1000):
```clojure
(generate-promise {:entropy 1000} println)
(println "Look ma, no blocking!") 
;; => "Look ma, no blocking!"
;; => (1134 -419 16631 -2872 ...)
```
Keep in mind, in this case the first parameter option map is required.  You can pass an empty map to use defaults.

Alternatively, you can use `generate-async` which will return a channel from which you can asynchronously take from. This allows you to be more flexible with coordination, but will require 
you to also require the core.async dependency:
```clojure
(ns my.test
  (:require [winkler.core :refer [generate-async]]
            [clojure.core.async :as a]))

(let [ch (generate-async {:entropy 1000})]
(a/go-loop []
    (if-let [entropy (a/<! ch)]
    (do (println entropy)
        (recur)))))
```
The channel will close once the harvest limit is reached.

## What's it doing?

The entropy generation technique is ~~ripped off~~ *inspired* by keybase's [more-entropy](https://github.com/keybase/more-entropy) js library.  The library runs a sequence of floating point operations in a given time limit, generating a random integer based off the number of successful operations.  Whereas other client-side techniques involve connecting to the DOM to collect entropy through user events, this technique is more platform-agnostic and relies on the entropy state of the machine a la linux's `/dev/random`.

## Should I use this to bootstrap my new crypto startup?

Short answer: No.

Longer answer: This, combined with more cryptographically secure algorithms provided client-side such as `window.crypto`, will provide much, much more security than using psuedo-random number generators such as `Math.random()`. [Read more here](https://stackoverflow.com/questions/578700/how-trustworthy-is-javascripts-random-implementation-in-various-browsers).  Although the V8 implementation (and most other js engines) moved to the speedy XorShift128+ LSFR, there is an inherent limitation with these types of generators.  While it is suitable for simple generation and games, more entropy is needed for stronger cryptography.

Disclaimer: I am nowhere near an expert in this field.

## Contributing

Open an issue, talk it out, open a pull request, and bear the fruits of your labor. This is not my code. It is *our* code, comrade.

## Roadmap

1. Migrate files to .cljc for full compatability with Clojure as well. 
2. (Maybe?) Build and publish this library on npm to allow integration with other JS projects. Shadow-cljs provides great tools on this front, however some work is needed as this is still a purely clojurescript project. 