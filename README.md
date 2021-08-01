# Drafter
A tool for rapid workflow prototyping with java programs. 
This is primarily targetting towards small repetitive tasks, such as programming contests and bulk productions.

# Usage
First, there is a simple syntax that can be used when most of these arguments are irrelevant to you:

`./simple.sh --classname my.package.working.Class --auxiliary-package-name my.other.package.aux`

This can be simplified by replacing `classname` and `auxiliary-package-name` with the shorthands `-c` and `-a`.

for a demonstration of how the tool works, run `test.sh`. This will create a project ready to be run, which takes as input a file, and produces as output the lines of that file. All the contents of the sample project exist in the files out.txt, solve.txt, post.txt, params.txt, Inject.java and command.txt. to learn about these files, run `drafter.sh -h`.

For a more complete script, run the primary tool, drafter.

./drafter.sh 

[ Mandatory Arguments ]
* (-n --class-name) [classname]
* (-p --package-name) [packagename]
* (--location --primary-directory -l) [package directory]
* (-ap --aux-package-name) [auxiliary packagename]
* (--auxiliary-directory -ap) [auxiliary directory]

[ Optional Arguments ]
* (--additional-imports) [additional imports file]
* (--insert-code) [file to insert]
* (--overwrite-aux) If set, auxiliary files will be overwritten where possible
* (--overwrite-main) If set, the main file specified will overwrite any previous versions
* \+ (--debug --debul-level -d) [debug level]: Between 0 and 5, with 0 being not at all debug. Without an argument, this defaults to 1. If not set, 0.
* \+ (--help --show-help -h) : If set, the full usage dialog will be displayed.
* \+ (--disable-colors -dc) : if set, colors are disabled
* \+ (--ignore-remaining) : do-nothing terminal argument. If a termainal argument is set, all remaining arguments will be ignored.

# Output
This will generate a class with name [classname] in package [packagename] in directory [package directory]. Auxiliary files will be generated in [auxiliary-directory] with the package [axuiliary packagename].

The following arguments are enabled by default in a generated program:
* debug: set the debug level, and selectively enable/disable debug output
* help: display help/usage
* disable-colors: disable colored output
* ignore-remaining: ignore remaining output

The generated file can be immediately compiled and worked upon.

Additionally, the following may occur:
* If the insert-code command was used, the inserted code will appear in a block at the subclass level.
* If the additional-imports command was used, those imports will be inserted at the top of the file.

# JAVADOC
Javadoc for this can be seen at https://nbkelly.github.io/Drafter/com/nbkelly/Drafter.html
