#include<stdio.h>
main()
{
int n,i,num;
int maior=0;
printf("Quantos numeros deseja inserir:");
scanf("%d", &n);
for(i=1;i<=n;i++)
{
printf("Insira os seus numeros:");
scanf("%d", &num);
}
if(num*2==num || num/2==num)
{
}
else
maior++;
printf("%d\n", maior);
return 0;
}
