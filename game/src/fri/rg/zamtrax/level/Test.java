package fri.rg.zamtrax.level;

import zamtrax.Component;

public class Test extends Component {

	@Override
	public void update(float delta) {
		System.out.println(getTransform().getPosition());
	}

}
