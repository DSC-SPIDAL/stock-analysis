package Salsa.Core;

public class OnDemandComputation<T>
{
	private OnDemandCompute<T> _compute;
	private T _result;
	private boolean _shouldCompute;

	public OnDemandComputation(OnDemandCompute<T> compute)
	{
		_compute = new OnDemandCompute()
		{
			@Override
			public <T> T invoke()
			{
				return compute();
			}
		};
		_shouldCompute = true;
	}

	public final boolean getShouldCompute()
	{
		return _shouldCompute;
	}

	public final T Compute()
	{
		if (_shouldCompute)
		{
			_result = Compute();
			_shouldCompute = false;
		}

		return _result;
	}

	public final void Reset()
	{
		_result = null;
		_shouldCompute = true;
	}
}