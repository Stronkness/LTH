import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;

import client.view.ProgressItem;
import client.view.StatusWindow;
import client.view.WorklistItem;
import network.Sniffer;
import network.SnifferCallback;
import rsa.Factorizer;
import rsa.ProgressTracker;

public class CodeBreaker implements SnifferCallback {

    private final JPanel workList;
    private final JPanel progressList;
    
    private final JProgressBar mainProgressBar;
    private static int progressTask = 0;
    
    private final ExecutorService pool;
    


    // -----------------------------------------------------------------------
    
    private CodeBreaker() {
        StatusWindow w  = new StatusWindow();

        workList        = w.getWorkList();
        progressList    = w.getProgressList();
        mainProgressBar = w.getProgressBar();
        
        pool = Executors.newFixedThreadPool(2);
        
        w.enableErrorChecks();
    }
    
    // -----------------------------------------------------------------------
    
    public static void main(String[] args) {

        /*
         * Most Swing operations (such as creating view elements) must be performed in
         * the Swing EDT (Event Dispatch Thread).
         * 
         * That's what SwingUtilities.invokeLater is for.
         */

        SwingUtilities.invokeLater(() -> {
            CodeBreaker codeBreaker = new CodeBreaker();
            new Sniffer(codeBreaker).start();
        });
    }
    
    
    private static class Tracker implements ProgressTracker {
    	private ProgressItem prog;
    	private JProgressBar mainProgressBar;
    	public Tracker(ProgressItem prog, JProgressBar mainProgressBar) {
    		this.prog = prog;
    		this.mainProgressBar = mainProgressBar;
    		progressTask += 1000000;
    		SwingUtilities.invokeLater(() -> {
        		mainProgressBar.setMaximum(progressTask);
    		});

    	}
        private int totalProgress = 0;

        /**
         * Called by Factorizer to indicate progress. The total sum of
         * ppmDelta from all calls will add upp to 1000000 (one million).
         * 
         * @param  ppmDelta   portion of work done since last call,
         *                    measured in ppm (parts per million)
         */
        @Override
        public void onProgress(int ppmDelta) {
            totalProgress += ppmDelta;
    		SwingUtilities.invokeLater(() -> {
                prog.getProgressBar().setValue(totalProgress);
                mainProgressBar.setValue(ppmDelta + mainProgressBar.getValue());
    		});
        }
        
        public int getProgress() {
        	return totalProgress;
        }
    }
   

    // -----------------------------------------------------------------------

    private void addRemoveButton(JButton cancel, ProgressItem prog) {
    	JButton bRemove = new JButton("Remove");
        bRemove.addActionListener(e2 -> {
        	progressList.remove(prog);
        	progressTask -= 1000000;
        	mainProgressBar.setValue(mainProgressBar.getValue()-1000000);
        	mainProgressBar.setMaximum(progressTask);
        	
        });
        prog.remove(cancel);
        prog.add(bRemove);
    }
    
    private void actionBreak(String message, BigInteger n, WorklistItem item) {
    	ProgressItem prog = new ProgressItem(n, message);
    	
    	workList.remove(item);
    	progressList.add(prog); 
    	    	
		JButton cancel = new JButton("Cancel");
    	
    	Runnable task = () -> {
    		ProgressTracker tracker = new Tracker(prog, mainProgressBar);

            try {
				String plaintext = Factorizer.crack(message, n, tracker);
				
				SwingUtilities.invokeLater(() -> {
	                prog.getTextArea().setText(plaintext);
	                addRemoveButton(cancel, prog);
				});

                
			} catch (InterruptedException e1) {
				SwingUtilities.invokeLater(() -> {
					int progressLeft = 1000000 - ((Tracker)tracker).getProgress();
					mainProgressBar.setValue(mainProgressBar.getValue() + progressLeft);
					prog.getProgressBar().setValue(1000000);
					prog.getTextArea().setText("[cancelled]");
	                addRemoveButton(cancel, prog);
				});
			}
            
    	};
    	
    	Future<?> future = pool.submit(task);
    	

		cancel.addActionListener(e -> {
			future.cancel(true);
		});
		prog.add(cancel);
		
    }
    
    
    /** Called by a Sniffer thread when an encrypted message is obtained. */
    @Override
    public void onMessageIntercepted(String message, BigInteger n) {
        System.out.println("message intercepted (N=" + n + ")...");
        
        SwingUtilities.invokeLater(() -> {
        	JButton button = new JButton("Break");
            WorklistItem item = new WorklistItem(n, message);
            
            button.addActionListener(e -> actionBreak(message, n, item));
            
    	            item.add(button);
    				workList.add(item);

        });



    }
}
