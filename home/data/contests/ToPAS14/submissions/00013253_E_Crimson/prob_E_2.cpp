#include <iostream>
#include <string.h>

using namespace std;

int main(){

    int n,a,c,i,j;
    cin >> n;

    string s[n+1],out="",st;

    for( i=1; i<=n; ++i){
        cin >> s[i];
    }

    do{
        cin>>a;
        switch(a){
            case 1:{
                cin>>c;
                for( i=0; i<c; ++i){
                    cin>>st;

                    for( j=1; j<=n; ++j){
                        if (st==s[j]){
                            cout<<j;
                            if(i<c-1)
                                cout<<' ';
                            else
                                cout<<endl;
                        }
                    }
                }
                break;
            }

            case 2:{
                cin >> c;
                int x;
                for(i=0; i<c; ++i){
                    cin>>x;
                    cout<<s[x];
                    if(i<c-1)
                        cout<<' ';
                    else
                        cout<<endl;
                }
                break;
            }

            default: break;
        }
    }while(a!=0);

    return 0;
}
