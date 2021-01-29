# Drafter
A tool for rapid workflow prototyping with java programs. 
This is primarily targetting towards small repetitive tasks, such as programming contests and bulk productions.

# Usage
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
* If the additional-umports command was used, those imports will be inserted at the top of the file.

# TODO
Show an example of this being used.
