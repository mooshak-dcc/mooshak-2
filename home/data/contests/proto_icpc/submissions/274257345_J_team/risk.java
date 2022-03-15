
import java.io.*;
import java.util.*;
import java.util.Arrays;
import java.text.NumberFormat;

//	N clients
//	each client:	ThreshHold	MReceipts
//	each receipt:	ClientID	Value	InitialDay	FinalDay

class risk {
    // static DataInputStream stdin;
    static BufferedReader stdin;
    static StreamTokenizer st;
    static NumberFormat nf;

    static final int max_cli=100;		// maximum number of clients
    static final int max_doc=100;		// maximum number of document per clientaaaaaa
    
    

    static int n;				// number clients
    static int m;				// number documents of last client
    static int  vl[] = new int[max_doc];	// document's value
    static int  it[] = new int[max_doc];	// document's initial day
    static int  fn[] = new int[max_doc];	// document's final day

    static int  np[] = new int[2*max_doc];	// notable points

    static int tr;				// total risk
    static int tur;				// total uncovered riskw

    public static void main(String args[]) {
       nf    = NumberFormat.getNumberInstance(Locale.US);
       // stdin = new DataInputStream(System.in); 	// deprecated
       stdin = new BufferedReader(new InputStreamReader(System.in));
       st    = new StreamTokenizer(stdin);

       nf.setMaximumFractionDigits(2);
       nf.setMinimumFractionDigits(2);

       process_all_clients();
       
       System.out.println(nf.format(tur*100.0/tr)+"%");
    }
    
    public static void   process_all_clients() {
	int i,j;
	int n, m;
	int dl;

	n=read_int();
	for(i=0;i<n;i++) {
	    dl=read_int();
	    m=read_int();

	    for(j=0;j<m;j++) {
		vl[j]=read_int();
		it[j]=read_int();
		fn[j]=read_int();
		np[2*j]=it[j];
		np[2*j+1]=fn[j];
	    }
	    process_each_client(dl,m);		
	}
    }

    static void process_each_client(int dl, int m) {
	int i,j,sv;

	Arrays.sort(np,0,2*m);	

	// run  each segment
	for(i=0; i<2*m-1; i++) {
	    // compute area
	    sv=0;
	    for(j=0; j<m; j++) 
		if(it[j] <= np[i] && fn[j] >= np[i+1]) 
		    sv += vl[j];
		
	    tr += (np[i+1]-np[i])*sv;
	    if(sv>dl) 
		tur += (np[i+1]-np[i])*(sv-dl);
	}	
    }
    
    private static int read_int() {

	try {
	    st.nextToken();
	    return (int) st.nval;
	} catch(Exception e) {
	    System.out.println("Erro: "+e.getMessage());
	    return -1;
	}
    }



}

