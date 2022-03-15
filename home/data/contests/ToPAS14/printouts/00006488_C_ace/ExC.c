#include<stdio.h>
main()
{
int y,x,n,i;
int ymaior=0;
int xmenor=800;
printf("Quantas coordenadas que inserir:");
scanf("%d",&n);
for(i=1;i<=n;i++)
{
printf("Insira as coordenadas(Y,X):");
scanf("%d %d",&y,&x);
if(y>ymaior)
{
ymaior=y;
}
if(x<xmenor)
{
xmenor=x;
}
}
printf("%d %d\n",ymaior,xmenor);
return 0;
}
