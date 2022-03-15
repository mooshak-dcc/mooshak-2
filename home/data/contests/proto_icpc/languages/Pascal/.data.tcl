set        Fatal {Compilador nao é executavel <code>Compile</code>: /usr/bin/fpc<br>}
set      Warning {}
set         Name Pascal
set    Extension pas
set     Compiler {Free Pascal}
set      Version 1.0.4
set      Compile {/usr/bin/fpc -v0w -oprog $file}
set      Execute prog
set         Data {}
set         Fork {}
set         Omit {^Free Pascal Compiler.*\nCopyright.* by Florian Klaempfl( and others)?\n/usr/bin/ld(.*): warning: link.res contains output sections; did you forget -T\?$}
