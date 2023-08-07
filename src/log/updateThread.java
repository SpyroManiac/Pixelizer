package log;

public class updateThread extends Thread{
    LogCreator aa;
    Integer i;
    boolean end;
    public updateThread(LogCreator abc)
    {
        aa = abc;
        i = 0;
    }

    public void endThread() {
        end = false;
    }

    @Override
    public void run()
    {
        end = true;
        while(end)
        {
            try
            {
                sleep(100);
            }
            catch (InterruptedException e)
            {
                aa.setText(LogCreator.getLog() + e.getMessage());
            }
            aa.setText(LogCreator.getLog());
            i++;
        }
    }

}