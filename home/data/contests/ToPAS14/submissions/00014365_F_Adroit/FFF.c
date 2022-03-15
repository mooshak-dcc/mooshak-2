
#include <stdio.h>

int main()
{
	int s,m,n,i,j,d,k,pix,piy,jjj;
	char mat[21][21],g;
	scanf("%d %d %d",&s,&m,&n);
	for(i=0;i<m;i++)
	{
		for(j=0;j<n;j++)
		{
			scanf(" %c",&mat[i][j]);
		}
	}
	scanf("%d",&jjj);
	for(i=0,k=1;i<jjj && mat[pix][piy]!='F';i++)
	{
		scanf(" %c",&g);
		scanf("%d",&d);
		if(g=='E')
		{
			pix=pix+d;
	    }
	    else
			if(g=='W')
			{
				pix=pix-d;
			}
			else
				if(g=='N')
				{
					piy=piy-(d*k);
			    }
			    else
					if(g=='S')
					{
						piy=piy+(d*k);
					}
		if(piy>=m-1)
		{
			piy=m-1;
		}
		if(pix>=n-1)
		{
			pix=n-1;
		}		
		if(piy==m-1 || pix==0)
		{
			k=k*(-1);
		}
		
			
		if(mat[pix][piy]=='X')
			k=k*-1;
		else
			if(mat[pix][piy]=='I')
			{
				s=s+(0.05*s);
			}
			else
				if(mat[pix][piy]=='T')
				{
					s=s-(0.1*s);
				}
					else
						if(mat[piy][pix]=='A')
						{
							s=s+10;
						}
	}
		if(mat[piy][pix]=='F')
	{
		printf("Aniquilado\n");
	}
	else
		if(mat[piy][pix]=='A')
		{
			printf("Fora(%d,%d):%d:",piy+1,pix+1,s);
			if(k==-1)
			{
				printf("D\n");
			}
			else
				printf("ND\n");
		}	
		else
			{
				printf("Dentro(%d,%d):%d:",piy+1,pix+1,s);
				if(k==-1)
				{
					printf("D\n");
				}
				else
					printf("ND\n");	
			}
	return 0;
}

