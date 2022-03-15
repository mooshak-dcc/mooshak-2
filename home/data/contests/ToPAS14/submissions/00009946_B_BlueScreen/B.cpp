#include <iostream>

using namespace std;

int main() {
	int quant;
	cin >> quant;	

	int v[quant];

	int c = 0;
	for(int x=0;x<quant;x++)
	{
		cin >> v[x];
	}
	for(int x=1; x < quant; x++)
	{
		if((v[x-1]*2) < v[x] && (v[x+1]*2)< v[x])
		{
			c++;
		}
 	}	
	cout << c << endl;
}
