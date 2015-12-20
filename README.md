# Tabulas
*System to manage human-readable tables using files*


[![Build Status](https://travis-ci.org/julianmendez/tabulas.png?branch=master)](https://travis-ci.org/julianmendez/tabulas)


Tabulas is a system to manage human-readable tables using files. It uses a specific type of file format that is similar to a Java .properties, but allows defining the same property for different objects.
Tabulas is an experimental semi-automatic Scala reimplementation of Tabula, which is implemented in Java.


## Source code

To clone and compile the project:

```
$ git clone https://github.com/julianmendez/tabulas.git
$ cd tabulas
$ mvn clean install
```


## Author
[Julian Mendez](http://lat.inf.tu-dresden.de/~mendez/)


## License

This software is distributed under the [Apache License Version 2.0](http://www.apache.org/licenses/LICENSE-2.0.txt).


## Example

```properties
# simple format 1.0.0


type = record 


def = \
 id:String \
 title:String \
 authors:List_String \
 web:URI \
 documents:List_URI


order = \
 id


new = 
id = arXiv:1412.2223
title = A topological approach to non-Archimedean Mathematics
authors = \
 Vieri_Benci \
 Lorenzo_Luperi_Baglini
web = http://arxiv.org/abs/1412.2223
documents = \
 http://arxiv.org/pdf/1412.2223#pdf \
 http://arxiv.org/ps/1412.2223#ps \
 http://arxiv.org/format/1412.2223#other


new = 
id = arXiv:1412.3313
title = Infinitary stability theory
authors = \
 Sebastien_Vasey
web = http://arxiv.org/abs/1412.3313
documents = \
 http://arxiv.org/pdf/1412.3313#pdf \
 http://arxiv.org/ps/1412.3313#ps \
 http://arxiv.org/format/1412.3313#other


```
