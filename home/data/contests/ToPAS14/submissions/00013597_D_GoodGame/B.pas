Program B;
Var N, Contador, I :integer;
    Sinal : ARRAY [1..1000] of integer;
Begin
Randomize;
Contador:=0;
        Repeat
        Readln (N);
        Until (N >= 3) AND (N <= 1000);
        For I := 1 to N DO
        Sinal[I] := RANDOM (32)+1;

        For I:= 1 to N DO
        If (Sinal [I-1] + Sinal [I-1] < Sinal [I]) and (Sinal [I+1] + Sinal [I+1] < Sinal [I]) then
        Contador := Contador + 1;

        For I:= 1 to N DO
        Begin
                Writeln (Sinal[I]);
        END;
        Writeln (Contador);
        READLN;
        END.