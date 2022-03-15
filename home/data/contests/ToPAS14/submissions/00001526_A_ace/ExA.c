#include<stdio.h>
main()
{
int ano,mes,dia,total;
//printf("Incira data de nascimento d/m/a:");
printf("Incira o dia em que nasceu:");
scanf("%d",&dia);
printf("Incira o mes em que nasceu:");
scanf("%d",&mes);
printf("Incira o ano em que nasceu:");
scanf("%d",&ano);
//scanf("%d %d %d",&dia,&mes,&ano);
printf("%d %d %d\n",dia,mes,ano);
ano=2014-ano;
ano=ano*360;
mes=12-mes;
mes=mes*30;
dia=30-dia;
total=ano+mes+dia;
printf("%d\n",total);
return 0;
}


