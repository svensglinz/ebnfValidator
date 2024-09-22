# ebnfValidator 

A brute force parser for any kind of EBNF grammar that validates whether a partiular code snippet is syntactically valid or not. 
The parser uses a top-down approach and evaluates all possible productions. 

The syntax for EBNF grammar is adjusted to correspond to the one used in the course **Introduction to Programming** at ETH Zurich. 

As the parser could get trapped in infinite recursion, the search for a valid production is stopped after a maximum level of depth, which can lead 
to incorrect results for heavily nested productions. 

For more context and background, see [here](https://svenglinz.ovh/post.php?id=18)

## Installation
```bash
mkdir $HOME/ebnfValidator
curl https://raw.githubusercontent.com/svensglinz/ebnfValidator/master/ebnfValidator.jar > $HOME/ebnfValidator/ebnfValidator.jar
# echo "alias ebnfValidator='java -jar $HOME/ebnfValidator/ebnfValidator.jar' >> ~/.bashrc
```

## Uninstall
```bash
rm -r $HOME/ebnfValidator
# remove alias from ~/.bashrc if it was set
```

## Usage 

First, create a text file that describes your grammar.

- Each rule name must be wrapped in <>
- Each line consists of one rule in the form of <RULENAME> <= RULE DESCRIPTION
- each description can contain: literals, repetition {}, option [] or selection |
- selection has the highest precedence before all other operators <br> <br>
 *eg: \<RULE1> <= a \<RULE1> | b is parsed as \<RULE1> <= (a \<RULE1> ) | b (ie. as a selection between a AND <RULE1> or b*
- The entry point into the grammar is always a rule called **expression**. If you do not have this rule ( or it is not the top-level rule), the results will be incorrect


LIMITATIONS: 

- literals (eg. b in the following) can only be added individually <br> <br>
*Eg. If you want a grammar such as *\<STRING> <= {\<CHARACTER>}* where *\<CHARACTER>* can be any letter in the alphabet,
you will have to list each letter separately ie:
\<CHARACTER> <= a | b | c | ... | z*

For an example, see [here](/example/grammar.txt)

Then evaluate any expression against your grammar in the interactive shell

```bash
ebnfValidator grammar.txt
```
> Welcome to the Interactive Shell! </code>
Type in any expression and check if matches against the provided Grammar Description
Type '.exit' to quit at any time.

```bash
a = 100
```
> ✔️ valid

```bash
b <- 123
```
> ✔️ valid

```bash
b == 123
```
> :x: invalid

```bash
.exit
```

> Goodbye!
