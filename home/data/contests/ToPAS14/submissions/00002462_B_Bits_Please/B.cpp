#include <iostream>
#include <stdio.h>
#include <string>
#include <string.h>
#include <algorithm>

using namespace std;

int main()
{
    int n,res=0,antes=0,depois=0,i;
    cin >> n;
    int v[n];
    for (i=0;i<n;i++)
    scanf("%d",&v[i]);
    for( i=1;i<n-1;i++)
    {

        antes=0;
        depois=0;
        if(v[i-1]*2<v[i])
        {antes=1;}
        if(v[i+1]*2<v[i])
        {depois=1;}
        if(antes==1 && depois==1)
        {res=res+1;}
    }
    cout << res << endl;
    return 0;
}
