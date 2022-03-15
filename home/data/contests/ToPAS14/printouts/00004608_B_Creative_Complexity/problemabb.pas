program problemaB;

uses crt, strutils, sysutils;
var

  v_in               : array [1..1001] of integer;

  n                  : integer;
  i                  : integer;
  k                  : integer;

  conta              : integer;

begin

  readln (n);

  conta := 0;

  For i := 1 to n do
    begin
      readln (v_in[i]);
    end;

  For k:= 2 to n-1 do
    begin

      If v_in[k] > v_in[k-1] * 2 then
        begin

          If v_in[k] >= v_in [k+1] * 2 then
            begin

              conta := conta + 1;

            end;

        end;

    end;

    If v_in[1] > v_in[2] * 2 then
      begin
        conta := conta + 1;
      end;

    If v_in [n] > v_in[n-1] * 2 then
      begin
        conta := conta + 1;
      end;

  writeln (conta);

end.