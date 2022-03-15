#include <stdio.h>
int c;
int dia,ano,mes;
int main()
{
scanf("%d %d %d", &ano, &mes, &dia);
if(mes<3)
{
	ano = ano-1;
	mes = mes + 12;
}
int d = (365.25 *(ano + 4716));
int e = (30.6001 *(mes +1));
	int a = ano/100;
	int b = a/4;
if(ano>=1582)
{
c=2-a+b;
}

if(ano<=1582)
{
	c=0;
}
int x = d+e+dia+c-1524;
printf("%d\n", x);

return 0;
}
