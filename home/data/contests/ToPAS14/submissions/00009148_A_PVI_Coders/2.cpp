#include <iostream>
#include <vector>
#include <string>

using namespace std;

int main() {
    int a,m,d;
    cin >> a >> m >> d;
    if (m < 3) {
        a--;
        m += 12;
    }
    int A = a/100;
    int B = A/4;
    bool cont = true;
    if (a <= 1582) {
        if (a < 1582){
            cont = false;
        }
        else if (a == 1582 and m <= 10) {
            if (m < 10) {
                cont = false;
            }
            else if (m==10 and d < 15) {
                cont = false;
            }

        }

    }
    int C = 0;
    if (cont == true) {
        C = 2-A+B;
    }
    int D = 365*(a+4716);
    int r1 = (a+4716)/4;
    D+= r1;
    int E = 30*(m+1);
    int r2 = (6001*(m+1))/10000;
    E+= r2;
    int DJ = D+E+d+C-1524;
    cout << DJ << endl;
    return 0;
}
