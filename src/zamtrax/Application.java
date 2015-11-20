package zamtrax;

interface Application {

	interface Listener {

		void create();

		void update(float delta);

		void render();

		void dispose();

	}

	void start();

	void exit();

	int getScreenWidth();

	int getScreenHeight();

	void setApplicationListener(Listener applicationListener);

}
