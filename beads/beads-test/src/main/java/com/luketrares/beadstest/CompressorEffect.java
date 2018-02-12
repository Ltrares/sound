package com.luketrares.beadstest;

public class CompressorEffect implements DemoEffect {
	// float wav_in, // signal
	double threshold = 0.5; // threshold (percents)
	double slope = 0.5; // slope angle (percents)
	double tla = .003; // lookahead (ms)
	double twnd = .001; // window time (ms)
	double tatt = 0.0001; // attack time (ms)
	double trel = .300; // release time (ms)


	
	@Override
	public void calculateBuffer(DemoElement element) {
		int n = element.getContext().getBufferSize();
		float sr = element.getContext().getAudioFormat().sampleRate;

		float[] wav_out = new float[ n ];
		
		for ( int chi = 0; chi < element.getChannelCount(); chi++ ) {
			float[] wav_in = element.getOutBuffer(chi);
			double  att = (tatt == 0.0) ? (0.0) : Math.exp (-1.0 / (sr * tatt));
		    double  rel = (trel == 0.0) ? (0.0) : Math.exp (-1.0 / (sr * trel));

		    // envelope
		    double  env = 0.0;

		    // sample offset to lookahead wnd start
		    int     lhsmp = (int) (sr * tla);

		    // samples count in lookahead window
		    int     nrms = (int) (sr * twnd);

		    // for each sample...
		    for (int i = 0; i < n; ++i)
		    {
		        // now compute RMS
		        double  summ = 0;

		        // for each sample in window
		        for (int j = 0; j < nrms; ++j)
		        {
		            int     lki = i + j + lhsmp;
		            double  smp;

		            // if we in bounds of signal?
		            // if so, convert to mono
		            if (lki < n)
		                smp = wav_in[lki]; //0.5 * wav[lki][0] + 0.5 * wav[lki][1];
		            else
		                smp = 0.0;      // if we out of bounds we just get zero in smp

		            summ += smp * smp;  // square em..
		        }

		        double  rms = Math.sqrt (summ / nrms);   // root-mean-square

		        // dynamic selection: attack or release?
		        double  theta = rms > env ? att : rel;

		        // smoothing with capacitor, envelope extraction...
		        // here be aware of pIV denormal numbers glitch
		        env = (1.0 - theta) * rms + theta * env;

		        // the very easy hard knee 1:N compressor
		        double  gain = 1.0;
		        if (env > threshold)
		            gain = gain - (env - threshold) * slope;

		        //wav_out[i] = (float)(wav_in[i]*gain);
		        //wav_out[i] = (float) Math.signum(wav_in[i]*(1.0 - Math.exp(-wav_in[i])));
		    } //for i
			
		    for (int i = 0; i < n; ++i) {
		    	//wav_in[i] = wav_out[i];
		    }
		    
		} //for chi
		

	}

}
