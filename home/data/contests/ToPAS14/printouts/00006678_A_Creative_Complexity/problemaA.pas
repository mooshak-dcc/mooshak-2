program problemaA;

uses crt, strutils, sysutils;

var

  ano                                : integer;
  mes                                : integer;
  dia                                : integer;

  data                               : string;

  a                                  : integer;
  b                                  : integer;
  c                                  : integer;
  d                                  : real;
  e                                  : real;

  DJ                                 : real;

begin

  readln (data);
  // readln (mes);
  // readln (dia);

  mes := StrToInt(Copy (data,6,1));
  dia := StrToInt(Copy (data,8,1));
  ano := StrToInt(Copy (data,1,4));
  data := IntToStr(dia) + '/' + IntToStr(mes) + '/' + IntToStr(ano);

    If mes < 3 then
      begin
       // writeln ('O meu ano e = ', ano);
        ano := ano - 1;
        //writeln ('O meu ano agora e = ',ano);
        //writeln ('O meu mes e = ',mes);
        mes := mes + 12;
      //  writeln ('O meu mes agora e = ', mes);
      end;
    //  writeln ('sai do if do mes');

  a := trunc(ano/100);
  b := trunc(a / 4);

 // writeln ('converti o a e o b');

 // writeln ('a = ', a);
  //writeln ('b= ', b);
  //writeln ('c= ',c);


   If data > '15/10/1582' then
     begin
       c := 2 - a + b;
      // writeln ('o meu c e = ',c);
     end;

   If data <= '4/10/1582' then
     begin;
       c := 0;
     end;

    // writeln ('entrei');

  d := 365.25 * (ano + 4716);
  // writeln ('o meu d e = ',d);
  e := 30.6001 * (mes + 1);
  // writeln ('o meu e e = ', e);

  DJ := d + e + dia + c - 1524;

  // writeln ('d=',d);
  // writeln ('e= ', e);
  writeln (trunc(DJ-1));

end.
