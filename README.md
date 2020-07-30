# winkler
[![Clojars Project](https://img.shields.io/clojars/v/imaginathansoft/winkler.svg)](https://clojars.org/imaginathansoft/winkler)

A moderately paranoid clojurescript library for generating more entropy. You may need a cryptographically secure RNG but who knows if it's enough.  This is just some extra salt.

## Installation

With Leinengen/Boot:
```
[imaginathansoft/winkler "0.1.1"]
```

With deps.edn:
```
imaginathansoft/winkler {:mvn/version "0.1.1"}
```

To build from source, run in the terminal:

```
git clone https://github.com/ndonolli/winkler.git
```

## Usage

There is one function in the core namespace named `generate` which will produce a lazy-seq of randomized integers. The sequence will produce until the total bits of entropy is greater than the provided argument amount.

```clojure
(require '[winkler.core :refer [generate]])

;;; Generate random integers with at least 100 bits of combined entropy
(generate :entropy 100) ;; => (1134 -419 16631 -2872 ...)

;; Without any arguments, `generate` will produce infinitely. So take precautions:
(take 3 (generate)) ;; => (5081 -1092 -4678)
;; Although lazy, each take does require running timed computations in order to calculate entropy values.
```
## What's it doing?

The entropy generation technique is ~~ripped off~~ *inspired* by keybase's [more-entropy](https://github.com/keybase/more-entropy) js library.  The library runs a sequence of floating point operations in a given time limit, generating a random integer based off the number of successful operations.  Whereas other client-side techniques involve connecting to the DOM to collect entropy through user events, this technique is more platform-agnostic and relies on the entropy state of the machine a la linux's `/dev/random`.

## Should I use this to bootstrap my new crypto startup?

Short answer: No.

Longer answer: This, combined with more cryptographically secure algorithms provided client-side such as `window.crypto`, will provide much, much more security than using psuedo-random number generators such as `Math.random()`. [Read more here](https://stackoverflow.com/questions/578700/how-trustworthy-is-javascripts-random-implementation-in-various-browsers).  Although the V8 implementation (and most other js engines) moved to the speedy XorShift128+ LSFR, there is an inherent limitation with these types of generators.  While it is suitable for simple generation and games, more entropy is needed for stronger cryptography.

Disclaimer: I am nowhere near an expert in this field.

## Contributing

Open an issue, talk it out, open a pull request, and bear the fruits of your labor. This is not my code. It is *our* code, comrade.

## Roadmap

1. Develop non-blocking versions of the generator using `core.async`. The core generator is fine for most use-cases (and lazy!) but an async version would suit for longer sequences.
2. Migrate files to .cljc for full compatability with Clojure as well. 
3. (Maybe?) Build and publish this library on npm to allow integration with other JS projects. Shadow-cljs provides great tools on this front, however some work is needed as this is still a purely clojurescript project. 

## Licence

winkler is [MIT licenced](license.txt)