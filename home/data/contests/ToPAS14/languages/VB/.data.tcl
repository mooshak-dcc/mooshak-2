set        Fatal {}
set      Warning {}
set         Name {Visual Basic}
set    Extension vb
set     Compiler Mono
set      Version 2.10.8
set      Compile {/usr/bin/vbnc -nologo $file}
set      Execute {/usr/bin/mono $name.exe}
set         Data {}
set         Fork 100
set         Omit (Assembly.*|Compilation.*)
set          UID 597
