package com.example.andrej.evolucijskoprog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.Arrays;
import java.util.Random;

//Deterministični algoritem je kadar daš noter x in dobiš ven y. Za enak x dobiš vendo enak y. Npr.: 1+1=2 na vhodu daš 1 + 1 in boš vedno na izhodu dobil 2
//stohastično je malce drugače, kljub temu, da vedno vnesemo enake vhodne podatke so na izhodu vedno drugačni podatki.
public class ActivityMain extends AppCompatActivity {

    private static Random rnd = new Random();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String[] stringArray = {"Sphere","Griewank","Perm"};
        Spinner spinner = (Spinner)findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter(this,android.R.layout.simple_spinner_item, stringArray);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        Button button = (Button) findViewById(R.id.button);
    }
    public void buttonClick(View v) {
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        EditText dim = (EditText) findViewById(R.id.editDim);
        EditText eval = (EditText) findViewById(R.id.editEval);
        EditText CR = (EditText)findViewById(R.id.editCR);
        EditText F =(EditText)findViewById(R.id.editF);
        EditText Pop_size=(EditText)findViewById(R.id.editPopSize);

        int dimenzija, evaluacija,pop_size;
        dimenzija = Integer.parseInt(dim.getText().toString());
        evaluacija = Integer.parseInt(eval.getText().toString());
        pop_size = Integer.parseInt(Pop_size.getText().toString());
        TextView textizpis = (TextView) findViewById(R.id.textIzpis);
        double cr = Double.parseDouble(CR.getText().toString());
        double f = Double.parseDouble(F.getText().toString());

        textizpis.setText("");
        double min = 0, max = 0, fv = 0;
        int spinnerpos = spinner.getSelectedItemPosition(), funkV = 0;

        if (spinnerpos == 0) {
            min = -5.12;
            max = 5.12;
            fv = Double.MAX_VALUE;
        } else if (spinnerpos == 1) {
            min = -600;
            max = 600;
            fv = Double.MAX_VALUE;
        }
        else if(spinnerpos==2){
            min=-dimenzija;
            max=dimenzija;
            fv = Double.MAX_VALUE;
        }
       /* for (int i = 0; i < evaluacija; i++) {
            double d[] = vrniRnd(dimenzija, min, max);
            if (spinnerpos == 0) funkV = sphere(d);
            else if (spinnerpos == 1) funkV = griewank(d);
            else if (spinnerpos == 2) funkV = perm(d);
            if (funkV < fv) {
                for (int k = 0; k < dimenzija; k++) {
                    d[k] = Math.round(d[k] * 1000000.0) / 1000000.0;
                }
                textizpis.append(i + "; " + Arrays.toString(d) + "; " + Math.round(funkV * 1000000.0) / 1000000.0 + "\n\n");
                fv = funkV;
            }
        }
        textizpis.append("Konec");*/
        double[] array = new double[30]; //v velikosti zagonov!
        double pov =0;
        for(int i =0;i<30;i++){
            array[i] = DE(pop_size,evaluacija,dimenzija,min,max,f,cr,spinnerpos);
        }
        for(double sum:array)pov+=sum;
        pov=pov/30;
        double stdO =0;
        for(int i=0;i<30;i++){
            stdO+=Math.pow((array[i]-pov),2);
        }
        stdO=Math.sqrt(stdO/30);
        textizpis.append("Povprečje:  "+String.valueOf(pov)+"\nStan_O:   "+ String.valueOf(stdO));


    }
    public static double sphere(double d[]){
        double r=0;
        for(int i=0;i<d.length;i++){
            r+=d[i]*d[i];
        }
        return r;
    }
    public static double griewank(double d[]){
        double fr =4000;
        double s=0;
        double p=1;
        double r=0;
        for(int i=0;i<d.length;i++){
            s+=d[i]*d[i];
        }
        for(int i=0;i<d.length;i++){
            p*=Math.cos(d[i]/Math.sqrt(i+1));
        }
        r=s/fr-p+1;
        return r;

    }
    public static double perm(double d[]){
        double b=0.5,sout=0;
        for(int i=0;i<d.length;i++){
            double sin=0;
            for(int j=0;j<d.length;j++){
                sin+=(Math.pow(j+1,i+1)+b)*(Math.pow(d[j]/(j+1),i+1)-1);
            }
            sout+=sin*sin;
        }
        return sout;
    }
    public static double[] vrniRnd(int dim,double min,double max){
        double r[] = new double[dim];
        for(int i=0;i<r.length;i++){
            r[i]=rnd.nextDouble()*(max-min)+min;
        }
        return r;
    }
    public static double DE(int pop_size,int evaluacij,int dim,double min,double max,double F,double CR, int spinnerpos){ //Differential_evolution
        double x[][] =new double[pop_size][];
        double f[] = new double[pop_size];
        double ff=0;
        double bestf = Double.MAX_VALUE;
        int eval =0;
        double bestx[] = new double[dim];

        for(int i=0;i < pop_size;i++) {
            x[i] = vrniRnd(dim,min,max);

            if (spinnerpos == 0) {//sphere
                f[i]=sphere(x[i]);
            } else if (spinnerpos == 1) {//greiwank
                f[i] = griewank(x[i]);
            } else if (spinnerpos == 2) {//perm
                f[i] = perm(x[i]);
            }
            eval++;
            if(bestf>f[i]){
                bestf=f[i];
                bestx =Arrays.copyOf(x[i],x[i].length);
            }
        }
        //na random izberemo 3 različna števila!
        int a,b,c;
        while(eval<=evaluacij){
            for(int i=0;i<pop_size;i++){
                a=rnd.nextInt(pop_size);
                while(a==i){
                    a=rnd.nextInt(pop_size);
                }
                b=rnd.nextInt(pop_size);
                while(a==b || b==i){
                    b=rnd.nextInt(pop_size);
                }
                c=rnd.nextInt(pop_size);
                while((a==b)||(a==c)||(b==c)||(c==i)){
                    c=rnd.nextInt(pop_size);
                }
                int R = rnd.nextInt(dim);
                double y[] = new double[dim];
                for(int k =0;k<dim;k++){
                    if((rnd.nextDouble()<CR)||(k==R)){ //Križanje vrednosti!
                        y[k]=x[a][k]+F*(x[b][k]-x[c][k]);
                    }
                    else {
                        y[k] = x[i][k];
                    }
                }
                if (spinnerpos == 0) {//sphere
                    ff=sphere(y);
                } else if (spinnerpos == 1) {//greiwank
                    ff = griewank(y);
                } else if (spinnerpos == 2) {//perm
                    ff = perm(y);
                }
                eval++;
                if(ff<f[i]){ //če je križana vrednost boljša si jo zapomnimo
                    f[i] = ff;
                    x[i] =Arrays.copyOf(y,y.length);
                    if(bestf>ff){
                        bestf=ff;
                        bestx=Arrays.copyOf(y,y.length);;
                    }
                }
                if(eval>=evaluacij)
                    break;
            }
        }
       /* for (int k = 0; k < dim; k++) {
            bestx[k] = Math.round(bestx[k] * 1000000.0) / 1000000.0;
        }*/
       // String rez = Arrays.toString(bestx) + "; " + Math.round(bestf * 1000000.0) / 1000000.0;
        return bestf;
    }
}
