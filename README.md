# ebnfValidator 
A brute force parser for any kind of ebnfValidator.EBNF grammar that validates whether a partiular code snippet is syntactically valid or not. 
The parser uses a top-down approach and evaluates all possible productions 

For more context, see [here](https://svenglinz.ovh/post.php?id=18)

## Install
```bash
> mkdir $HOME/ebnfValidator
> curl https://raw.githubusercontent.com/svensglinz/ebnfValidator/master/ebnfValidator.jar > $HOME/ebnfValidator/ebnfValidator.jar
# echo "alias ebnfValidator='java -jar $HOME/ebnfValidator/ebnfValidator.jar' >> ~/.bashrc
```

## Uninstall
```bash
> rm -r $HOME/ebnfValidator
# optionally remove alias
```

## Usage 

Create a text file that describes your grammar.
- Each rule name must be wrapped in <>
- Each line consists of one rule in the form of <RULENAME> <= RULE DESCRIPTION
- each description can contain: literals, repetition {}, option [] or selection |
- todo: precedence description

For an example, see [here](grammar.txt)

Then evaluate any expression against your grammar in the interactive shell

```bash
> ebnfValidator grammar.txt
```
> Welcome to the Interactive Shell! </code>
Type in any expression and check if matches against the provided ebnfValidator.Grammar Description
Type '.exit' to quit at any time.

```bash
> a = 100
```
> ✔️ valid

```bash
> b <- 123
```
> ✔️ valid

```bash
> b == 123
```
> :x: invalid
