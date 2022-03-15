#include <stdio.h>
#include <stdlib.h>

int main()
{
    int n,i,sum=0;
    scanf("%d",&n);
    int vec[n];
    for (i=0;i<n;i+=1)
    {
        scanf("%d",&vec[i]);
    }
    for (i=0;i<n;i+=1)
    {
        if (i!=0 && i!=n-1)
        {
            if (2*vec[i-1]<vec[i] && 2*vec[i+1]<vec[i])
            {
                sum+=1;
            }
        }
    }
    printf("%d\n",sum);
    return 0;
}
