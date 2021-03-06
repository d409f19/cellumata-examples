<p align="center" >
    <img src="https://raw.githubusercontent.com/d409f19/cellmata/master/logo.png" height="91px" 
    alt="The Cellmata Programming Language" 
    title="The Cellmata Programming Language">
</p>

![CircleCI branch](https://img.shields.io/circleci/project/github/d409f19/cellmata/master.svg)
![GitHub release](https://img.shields.io/github/release/d409f19/cellmata.svg)
![GitHub](https://img.shields.io/github/license/d409f19/cellmata.svg)

Cellmata is a simple programming language designed for concisely writing and rendering cellular automata. 
This project is developed on the basis of a semester-project at AAU by group d409f19. 

This repository contains the source code and files related to the Cellmata compiler. 

## Conway's Game of Life example
The popular _game_ Conway's Game of Life (CGOL) is a cellular automaton (CA) which emulates _life_.

```
world {
    size = 100 [wrap], 100 [wrap];
    tickrate = 30;
    cellsize = 8;
}

neighbourhood Moore {
    (-1, 1), (0, 1), (1, 1),
    (-1, 0),          (1, 0),
    (-1, -1), (0, -1), (1, -1)
}

state Dead (255, 255, 255) {
    if (count(Alive, Moore) == 3) {
        become Alive;
    }
}

state Alive (0, 0, 0) {
    let aliveNeighbours = count(Alive, Moore);
    if (aliveNeighbours < 2 || 3 < aliveNeighbours) {
        become Dead;
    }
}
```

This program consists of a world-, a neighbourhood-, and two state-declarations. 
The state-declarations define the logic to be executed upon evaluation each cell of that given state. 
Note how this resembles a function, but rather than specifying a `return`-statement, a `become`-statement specifies which state the cell should become.

Cellmata also defines a set of built-in functions, here `count(state, neighbourhood)` is used, which are discussed in the [language-guide](https://github.com/d409f19/cellmata/blob/master/specification/language-guide.pdf).

## Using Cellmata
You can get started by with writing you own cellular automata by downloading the compiler from the [release section](https://github.com/d409f19/cellmata/releases). The compiler features both a build-in interpreter and [Kotlin](https://kotlinlang.org/) target. If you are new to programming, or just want to get going quickly, the interpreter is the easiest way to get a cellular automaton running.

## Cellmata documentation
Cellmata is a semester-project, and as such, a report will be written. This report will be released around June, 2019.

A simple, but effective, [language-guide](https://github.com/d409f19/cellmata/blob/master/specification/language-guide.pdf) is available for understanding syntax and semantics.

## Contributing
Since the source is free and public, you are more than welcome to contribute to the project. No code-of-conduct has been released yet, so use your best judgement.

## License
All source-code is released under GNU GPL v2.0.
