#include<stdio.h>
main()
{
int nlin=24;
int vendas,i;
int total;
int am=201400;
for(i=1;i<=nlin;i++)
{

do
{
printf("Qual o valor que de vendas:");
scanf("%d",&vendas);
am++;
printf("%d - %d\n",am,vendas);
if(am>=201412 || am>=201512 || am>=201612)
{
am=am+100;
}
total=total+vendas;
}while(total!=1000);
if(total>1000)
{
printf("Ja passou do limite de vendas\n");
}
}
return 0;
}
