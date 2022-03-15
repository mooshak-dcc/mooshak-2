#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int main()
{
    int n,i,j,mytype,mylimits,flag = 0,found=0;
    char palavras[n][20],palavras2[n][20];
    scanf("%d",&n);
    for (i=0;i<n;i+=1)
    {
        scanf("%s",palavras[i]);
    }
    scanf("%d %d",&mytype,&mylimits);
    for (i=0;i<n;i+=1)
    {
        strcpy(palavras2[i],palavras[i]);
    }
    char decrypt[1000][20];
    int myvec[1000];
    while (mytype!=0)
    {
        if (mytype==1)
        {
            flag = 0;
            for (i=0;i<mylimits;i+=1)
            {
                scanf("%s",decrypt[i]);
            }
            for (i=0;i<mylimits;i+=1)
            {
                for (j=0;j<n;j+=1)
                {
                    if (strcmp(decrypt[i],palavras[j])==0)
                    {
                        if (flag!=0)
                        {
                            printf(" %d",j+1);
                        }
                        else
                        {
                            printf("%d",j+1);
                            flag = 1;
                        }
                        break;
                    }
                }
            }
            printf("\n");
        }
        else if (mytype==2)
        {
            found = 0;
            for (i=0;i<mylimits;i+=1)
            {
                scanf("%d",&myvec[i]);
            }
            for (i=0;i<mylimits;i+=1)
            {
                for (j=0;j<n;j+=1)
                {
                    if ((myvec[i]-1)==j)
                    {
                        if (found==mylimits-1)
                        {
                            printf("%s",palavras2[j]);
                        }
                        else
                        {
                            printf("%s ",palavras2[j]);
                        }
                        found+=1;
                        break;
                    }
                }
            }
            printf("\n");
        }
        scanf("%d",&mytype);
        if (mytype!=0)
        {
            scanf("%d",&mylimits);
        }
    }
    return 0;
}
