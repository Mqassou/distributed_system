package framework.registry;

public enum BalancingMode {
	roundRobin, balancingByCpu
	// rounRobin : répartition circulaire
	//balancingByCpu : répartition en fonction du taux d'utilisation du CPU
}
