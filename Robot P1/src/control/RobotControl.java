package control;

import robot.Robot;

//De Xing Teoh
//s3719485
//Robot Assignment for Programming 1 s1 2018
//Adapted by Caspar and Ross from original Robot code in written by Dr Charles Thevathayan
public class RobotControl implements Control {
	// we need to internally track where the arm is
	private int height = Control.INITIAL_HEIGHT;
	private int width = Control.INITIAL_WIDTH;
	private int depth = Control.INITIAL_DEPTH;

	private int[] barHeights;
	private int[] blockHeights;

	private Robot robot;

	// called by RobotImpl
	@Override
	public void control(Robot robot, int barHeightsDefault[], int blockHeightsDefault[]) {
		this.robot = robot;

		// some hard coded init values you can change these for testing
		this.barHeights = new int[] { 7, 3, 1, 7, 5, 3, 2 };
		this.blockHeights = new int[] { 3, 1, 2, 3, 1, 1, 1 };

		robot.init(this.barHeights, this.blockHeights, height, width, depth);
		// initialize the robot
		initialize();
		// loop to read the array backwards
		for (int i = block; i >= 0; i--) {
			// loops until source column is empty
			moveArm1(setInitialHeight());
			moveArm2(SRC_COLUMN);
			moveArm3(setPickUpDepth());
			robot.pick();
			moveArm3(MIN_DEPTH);
			moveArm1(setReturnHeight());
			moveArm2(dest);
			moveArm3(setDropDepth());
			robot.drop();
			moveArm3(MIN_DEPTH);
			// move onto next block
			block--;
		}
		// moves width back to 1 after loop finishes
		moveArm2(MIN_WIDTH);
	}

	// keeps track of current block
	private int block;
	// custom array to keep track and update heights
	private int[] columns = new int[MAX_WIDTH];
	// keeping track of where to put 3 blocks
	private int threeDest = 3;
	// keeping track of where to put blocks
	private int dest = 0;

	private void initialize() {
		// sets the height of every column into a columns array
		block = blockHeights.length - 1;
		for (int i = 0; i < barHeights.length; i++) {
			columns[i + 2] = barHeights[i];
		}
		int src = 0;
		for (int i = 0; i < blockHeights.length; i++) {
			src += blockHeights[i];
		}
		columns[9] = src;
	}

	private int setInitialHeight() {
		// sets the source height to move the arm above the source block
		int barMax = 0;
		for (int i = 0; i < columns.length - 1; i++) {
			if (barMax <= columns[i]) {
				barMax = columns[i];
			}
		}
		if (barMax < columns[9]) {
			barMax = columns[9];
		}
		// +1 so height will be one above the block
		return barMax + 1;
	}

	private int setPickUpDepth() {
		// checks to see if arm3 needs to be lowered to pick up block
		int dropTo = height - columns[9] - 1;
		columns[9] -= blockHeights[block];
		return dropTo;
	}

	private int setReturnHeight() {
		// checks which destination the block needs to go to
		int max = 0;
		if (blockHeights[block] == 1) {
			dest = 1;
		} else if (blockHeights[block] == 2) {
			dest = 2;
		} else {
			dest = threeDest;
		}
		// loop checks for arm collision
		for (int i = 0; i < dest; i++) {
			if (columns[i] > max) {
				max = columns[i];
			}
		}
		// loop checks for block collision
		for (int i = 9; i >= dest - 1; i--) {
			if (columns[i] + blockHeights[block] > max) {
				max = columns[i] + blockHeights[block];
			}
		}
		// updates block 3 destination bar
		if (dest > 2) {
			threeDest++;
		}
		// +1 to take into account that arm2 takes up one height
		return max + 1;
	}

	private int setDropDepth() {
		// checks to see how far arm2 needs to be lowered to not cause a collision
		int lower = width - 1;
		int dropTo = height - columns[lower] - 1 - blockHeights[block];
		columns[lower] += blockHeights[block];
		return dropTo;
	}

	private void moveArm1(int value) {
		// moves arm1 up
		if (height < value) {
			for (int i = height; i < value; i++) {
				robot.up();
				height++;
			}
		}
		// moves arm1 down
		if (height > value) {
			for (int i = height; i > value; i--) {
				robot.down();
				height--;
			}
		}
	}

	private void moveArm2(int value) {
		// moves arm2 forwards
		if (width < value) {
			for (int i = width; i < value; i++) {
				robot.extend();
				width++;
			}
		}
		// moves arm2 backwards
		if (width > value) {
			for (int i = width; i > value; i--) {
				robot.contract();
				width--;
			}
		}
	}

	private void moveArm3(int value) {
		// moves arm3 downwards
		if (depth < value) {
			for (int i = depth; i < value; i++) {
				robot.lower();
				depth++;
			}
		}
		// moves arm3 upwards
		if (depth > value) {
			for (int i = depth; i > value; i--) {
				robot.raise();
				depth--;
			}
		}
	}
}