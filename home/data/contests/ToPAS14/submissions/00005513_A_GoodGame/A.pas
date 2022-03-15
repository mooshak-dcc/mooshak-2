Program P_A;
Var ano, mes, dia :integer;
    A, B, C, E, D, DJ :real;
Begin
        Readln (ano,mes,dia);
        If mes < 3 then
        Begin
                ano := ano - 1;
                mes := mes + 12;
        End;
        A := ano /100;
        B := A / 4;

        If (ano > 1582) AND (mes > 10) AND (dia > 15) then
                C := 2 - A + B else  If (ano <= 1582) AND (mes <= 10) AND (dia <= 4) then C := 0;

        D := trunc (365.25 * (ano + 4716));
        E := trunc (30.6001 * (mes + 1));

        DJ := D + E + dia + C - 1524;

        Write (round(DJ));
End.
