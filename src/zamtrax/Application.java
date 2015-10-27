package zamtrax;

interface Application {

	void start();

	void exit();

	int getScreenWidth();

	int getScreenHeight();

	void setApplicationListener(ApplicationListener applicationListener);

}
