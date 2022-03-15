#include <stdlib.h>
#include <stdio.h>
#include <string.h>


int main()
{
	int x[200],y[200],xx[200],yy[200],n,k,dif,dif1,i,max;
	char tip[200][5];
	scanf("%d%d %s",&x[0],&y[0],tip[0]);
	scanf("%d",&n);
	for(i=1;i<n;i++)
	{
		scanf("%d %d %s",&x[i],&y[i],tip[i]);
	}
	k=0;
	max=10000;
	for(i=1;i<n;i++)
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
	for(i=1;i<n;i++)
	{
		if(strcmp(tip[0],tip[i])==0)
		{
			dif=abs(x[i]-x[0]);
			dif1=abs(y[i]-y[0]);
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
		for(i=0;i<k;i++)
		{
			printf("%d %d %s %d\n",xx[i],yy[i],tip[0],(xx[i]-x[0])*(xx[i]-x[0]) + (yy[i]-y[0])*(yy[i]-y[0]));
		}
	}
		
				
				
	return 0;
}

