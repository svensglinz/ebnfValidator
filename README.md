# ebnfValidator 
A brute force parser for any kind of ebnfValidator.EBNF grammar that validates whether a partiular code snippet is syntactically valid or not. 
The parser uses a top-down approach and evaluates all possible productions 

For more context, see [here](https://svenglinz.ovh/post.php?id=18)

## Install
```bash
mkdir $HOME/ebnfValidator
curl https://raw.githubusercontent.com/svensglinz/ebnfValidator/master/ebnfValidator.jar > $HOME/ebnfValidator/ebnfValidator.jar
# echo "alias ebnfValidator='java -jar $HOME/ebnfValidator/ebnfValidator.jar' >> ~/.bashrc
```

## Uninstall
```bash
rm -r $HOME/ebnfValidator
# optionally remove alias
```

## Usage 
1. Interactive shell mode 
```bash
ebnfValidator [PATH_TO_GRAMMAR]
```

2. Validate single statement
```bash
ebnfValidator [PATH_TO_GRAMMAR] [STATEMENT]
```
