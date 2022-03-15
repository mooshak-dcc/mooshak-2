
#include <stdio.h>
#include <string.h>
int main()
{
	int x,i,nl,l,j,num;
	char dic[50][50],pal[50];
	scanf("%d",&x);
	for(i=0;i<x;i++)
	{
		scanf(" %s",dic[i]);
	}

	do
	{
		scanf("%d",&l);
		if(l==1)
		{
			scanf("%d",&nl);
			for(i=0;i<nl;i++)
			{
				scanf(" %s", pal);
				for(j=0;j<x;j++)
				{
					if(strcmp(pal,dic[j])==0)
					{
						printf("%d",j+1);
					}
					
				}
				if(i!=nl-1)
					{
						printf(" ");
					}
			}printf("\n");
		}
		else
		{
			if(l==2)
			{
			scanf("%d",&nl);
			for(i=0;i<nl;i++)
			{
				scanf("%d",&num);
				printf("%s",dic[num-1]);
				if(i!=nl-1)
				{
						printf(" ");
				}
				
			}
			printf("\n");
			}
		}
	}while(l!=0);
	return 0;
}

