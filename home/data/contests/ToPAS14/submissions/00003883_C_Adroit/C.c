#include <stdlib.h>
#include <stdio.h>
#include <string.h>


int main()
{
	int x[200],y[200],xx[200],yy[200],n,k,dif,dif1,i,max;
	char tip[200][5];
	scanf("%d%d %s",&x[0],&y[0],tip[0]);
	scanf("%d",&n);
	for(i=1;i<n+1;i++)
	{
		scanf("%d %d %s",&x[i],&y[i],tip[i]);
	}
	k=0;
	max=10000;
	for(i=1;i<n+1;i++)
	{
		if(strcmp(tip[0],tip[i])==0)
		{
			dif=abs(x[i]-x[0]);
			dif1=abs(y[i]-y[0]);
			if(dif+dif1<max)
			{
				max=dif+dif1;
			}
		}
	}
	for(i=1;i<n+1;i++)
	{
		if(strcmp(tip[0],tip[i])==0)
		{
			dif=x[i]-x[0];
			if(dif<0)
			{
				dif=dif*-1;
			}
			dif1=y[i]-y[0];
			if(dif1<0)
			{
				dif1=dif1*-1;
			}
			if(dif+dif1==max)
			{
				yy[k]=y[i];
				xx[k]=x[i];
				k++;
			}
		}
	}
	if(k==0)
	{
		printf("Nenhum\n");
	}
	else
	{
			printf("%d %d %s %d\n",xx[0],yy[0],tip[0],(xx[0]-x[0])*(xx[0]-x[0]) + (yy[0]-y[0])*(yy[0]-y[0]));
	}		
	return 0;
}

