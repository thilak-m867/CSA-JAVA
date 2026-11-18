String[][] slots = new String[slotsPerFloor.length][];
for (int f = 0; f < slotsPerFloor.length; f++) {
    slots[f] = new String[slotsPerFloor[f]];
}

//Billing calculation: 
long diffMinutes = 90; // 1.5 hours
int hourlyRate = 20;

// CORRECT: Floating-point conversion with Math.ceil for ceiling slab billing
long billableHours = (long) Math.ceil((double) diffMinutes / 60.0);
double correctFee = billableHours * hourlyRate; // Yields 2.0 * 20 = 40.0
