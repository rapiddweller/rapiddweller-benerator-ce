# Regular Expression Support 

Benerator supports most common regular expression features and given a regular expression, it is able to generate strings that match the regular expression – even in a unique manner if required. Here is a full description of regular expression features recognized for data generation setup:

## Characters 

Digits (0-9) and US-ASCII letters (A-Z, a-z) are supported as they are. Special characters can be defined by their UNICODE number in octal or hexadecimal form:

```shell
\0n The character with octal value 0n (0 `<= n `<= 7)

\0nn The character with octal value 0nn (0 `<= n `<= 7)

\0mnn The character with octal value 0mnn (0 `<= m `<= 3, 0 `<= n `<= 7)

\xhh The character with hexadecimal value 0xhh

\uhhhh The character with hexadecimal value 0xhhhh

For control codes in general there is a special notation

\cx The control character corresponding to x (e.g. \cA for Ctrl-A)

and certain control codes have own representations:

\t The tab character ('\u0009')

\n The newline (line feed) character ('\u000A')

\r The carriage-return character ('\u000D')

\f The form-feed character ('\u000C')

\a The alert (bell) character ('\u0007')

\e The escape character ('\u001B')
```

Some characters have a special meaning in regular expressions, so if you want to use them as a plain character (and not in their regex-meaning), you need to escape them with a backslash:

```bash
.Dot

\-Minus sign

\^Circumflex

\$Dollar

\|'Or' sign

\(Left parenthesis

\)Right parenthesis

\[Left bracket

\]Right bracket

\{Left curly brace

\}Right curly brace

\\ Backslash character
```



## Character Classes 

A 'character class' defines a set of characters from which one can be chosen and is marked by surrounding brackets: []

```shell
[abc] a, b, or c (simple class)

[^abc] Any character except a, b, or c (negation)

[a-zA-Z] a through z or A through Z, inclusive (range). Note: This includes only US-ASCII letters, not special letters of your configured language

[a-d[m-p]] a through d, or m through p: [a-dm-p] (union)

[a-z&amp;&amp;[def]] d, e, or f (intersection)

[a-z&amp;&amp;[^bc]] a through z, except for b and c: [ad-z] (subtraction)

[a-z&amp;&amp;[^m-p]] a through z, and not m through p: [a-lq-z] (subtraction)

There are some popular predefined character classes:

. Any character (may or may not match line terminators)

\d A digit: [0-9]

\D A non-digit: [^0-9]

\s A whitespace character: [ \t\n\x0B\f\r]

\S A non-whitespace character: [^\s]

\w A word character: [a-zA-Z_0-9]

\W A non-word character: [^\w]

Quantifiers can be used to specify how many characters of a class (or other regular expression construct) should appear:

_X_?_X_, once or not at all

_X_*_X_, zero or more times

_X_+_X_, one or more times

_X_{_n_}_X_, exactly _n_ times

_X_{_n_,}_X_, at least _n_ times

_X_{_n_,_m_}_X_, at least _n_ but not more than _m_ times
```



## Operators 

```shell
XY_X followed by _Y_

_X_|_Y_ Either _X_ or _Y_

(_X_) X, as a group
```



## Frequently asked Questions 

For generating characters which appear in your language, but not in English (like German umlauts), you can use their unicode representation (e.g. \u00FC for 'ü').

Different implementations of regular expression parsers exist and many have slight differences. So, if you take a regular expression that worked on one parser and run it on another one, you may get an error message. Benerator users that do not construct a regular expression by themselves, but simply take on 'from the internet' observe the same effect: The most frequent fault is if someone wants to generate a character that has a special meaning in a regular expression and does not escape it with a backslash, e.g. \., \\, \-, \|, \[, \], \{, \}, …

An example: Some regex parsers recognize that the expression [A-]X could resolve to AX or A-. While others (like Benerator's parser) diagnose a malformed character class (a missing character behind the minus) and report an error. You can resolve this by escaping the minus sign: [A\-]X.