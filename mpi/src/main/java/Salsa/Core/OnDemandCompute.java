package Salsa.Core;

public interface OnDemandCompute
{
	<T> T invoke();
}