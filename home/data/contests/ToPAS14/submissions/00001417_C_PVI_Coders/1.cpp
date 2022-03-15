#include <iostream>
#include <vector>
#include <string>

using namespace std;

int main() {
    int c1,c2;
    string loja;
    cin >> c1 >> c2 >> loja;
    int N;
    cin >> N;
    int minc1=0, minc2=0;
    string minloja = "";
    int mindist = 10000000;
    for (int i = 0; i< N; i++) {
        int d1,d2;
        string dloja;
        cin >> d1 >> d2 >> dloja;
        int dist = (d1-c1)*(d1-c1)+(d2-c2)*(d2-c2);
        if (dist < mindist and dloja == loja) {
            mindist = dist;
            minc1 = d1;
            minc2 = d2;
            minloja = dloja;
        }
    }
    if (mindist == 10000000) {
        cout << "Nenhum" << endl;
    }
    else {
        cout << minc1 << " " << minc2 << " " << minloja << " " << mindist << endl;
    }

    return 0;
}
