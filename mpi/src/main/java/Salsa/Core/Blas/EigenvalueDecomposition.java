package Salsa.Core.Blas;

import Salsa.Core.*;

//C# TO JAVA CONVERTER NOTE: There is no Java equivalent to C# namespace aliases:
//using T = System.Double;

/** Eigenvalues and eigenvectors of a real matrix.
 
 If A is symmetric, then A = V*D*V' where the eigenvalue matrix D is
 diagonal and the eigenvector matrix V is orthogonal.
 I.e. A = V.Multiply(D.Multiply(V.Transpose())) and
 V.Multiply(V.Transpose()) equals the identity matrix.
 If A is not symmetric, then the eigenvalue matrix D is block diagonal
 with the real eigenvalues in 1-by-1 blocks and any complex eigenvalues,
 lambda + i*mu, in 2-by-2 blocks, [lambda, mu; -mu, lambda].  The
 columns of V represent the eigenvectors in the sense that A*V = V*D,
 i.e. A.Multiply(V) equals V.Multiply(D).  The matrix V may be badly
 conditioned, or even singular, so the validity of the equation
 A = V*D*Inverse(V) depends upon V.cond().
 
*/
public class EigenvalueDecomposition implements Serializable
{
//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#region Members

	/** Array for internal storage of nonsymmetric Hessenberg form.
	*/
	private double[][] H;

	/** Array for internal storage of eigenvectors.
	*/
	private double[][] _V;

	/** Arrays for internal storage of eigenvalues.
	*/
	private double[] _d;

	/** Arrays for internal storage of eigenvalues.
	*/
	private double[] _e;

	/** Symmetry flag.
	*/
	private boolean _isSymmetric;

	/** Row and column dimension (square matrix).
	*/
	private int _n;

	/** Working storage for nonsymmetric algorithm.
	*/
	private double[] ort;

//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [NonSerialized] private double cdivi;
	private double cdivi;
//C# TO JAVA CONVERTER TODO TASK: Java annotations will not correspond to .NET attributes:
//ORIGINAL LINE: [NonSerialized] private double cdivr;
	private double cdivr;

//C# TO JAVA CONVERTER TODO TASK: There is no preprocessor in Java:
		///#endregion

	private OnDemandComputation<Matrix<Double>> _blockDiagonalOnDemand;
	private OnDemandComputation<Matrix<Double>> _eigenVectorsOnDemand;

	/** Check for symmetry, then construct the eigenvalue decomposition
	 Provides access to D and V
	 @param Arg Square matrix
	*/
	public EigenvalueDecomposition(Matrix<Double> Arg)
	{
		double[][] A = Arg;
		_n = Arg.getColumnCount();
		_V = Matrix<Double>.CreateElements(_n, _n);
		_d = new double[_n];
		_e = new double[_n];

		_isSymmetric = true;

		for (int j = 0; (j < _n) & _isSymmetric; j++)
		{
			for (int i = 0; (i < _n) & _isSymmetric; i++)
			{
				_isSymmetric = (A[i][j] == A[j][i]);
			}
		}

		if (_isSymmetric)
		{
			for (int i = 0; i < _n; i++)
			{
				for (int j = 0; j < _n; j++)
				{
					_V[i][j] = A[i][j];
				}
			}

			SymmetricTridiagonalize();

			SymmetricDiagonalize();
		}
		else
		{
			H = Matrix<Double>.CreateElements(_n, _n);
			ort = new double[_n];

			for (int j = 0; j < _n; j++)
			{
				for (int i = 0; i < _n; i++)
				{
					H[i][j] = A[i][j];
				}
			}

			NonsymmetricReduceToHessenberg();

			NonsymmetricReduceHessenberToRealSchur();
		}

		InitOnDemandComputations();
	}

	/** Constructs the eigenvalue decomposition from a symmetrical,
	 tridiagonal matrix.
	*/
	public EigenvalueDecomposition(double[] d, double[] e)
	{
		// TODO: unit test missing for EigenvalueDecomposition constructor.

		_n = d.length;
		_V = Matrix<Double>.CreateElements(_n, _n);

		_d = new double[_n];
		System.arraycopy(d, 0, _d, 0, _n);

		_e = new double[_n];
		System.arraycopy(e, 0, _e, 1, _n - 1);

		for (int i = 0; i < _n; i++)
		{
			_V[i][i] = 1;
		}

		SymmetricDiagonalize();

		InitOnDemandComputations();
	}

	/** Gets the real part of the eigenvalues.
	 @return real(diag(D))
	*/
	public final double[] getRealEigenValues()
	{
		return _d;
	}

	/** Gets the imaginary part of the eigenvalues
	 @return imag(diag(D))
	*/
	public final double[] getImagEigenValues()
	{
		return _e;
	}

	/** Gets the block diagonal eigenvalue matrix
	*/
	public final Matrix<Double> getBlockDiagonal()
	{
		return _blockDiagonalOnDemand.Compute();
	}

	/** Returns the eigenvector matrix
	*/
	public final Matrix<Double> getEigenVectors()
	{
		return _eigenVectorsOnDemand.Compute();
	}

	private void InitOnDemandComputations()
	{
		OnDemandCompute tempVar = new OnDemandCompute()
		{
			@Override
			public <T> T invoke()
			{
				return ComputeBlockDiagonalMatrix();
			}
		};
		_blockDiagonalOnDemand = new OnDemandComputation<Matrix<Double>>(tempVar);
		OnDemandCompute tempVar2 = new OnDemandCompute()
		{
			@Override
			public <T> T invoke()
			{
				return ComputeEigentVectors();
			}
		};
		_eigenVectorsOnDemand = new OnDemandComputation<Matrix<Double>>(tempVar2);
	}

	private Matrix<Double> ComputeBlockDiagonalMatrix()
	{
		double[][] D = Matrix<Double>.CreateElements(_n, _n);

		for (int i = 0; i < _n; i++)
		{
			for (int j = 0; j < _n; j++)
			{
				D[i][j] = 0.0;
			}

			D[i][i] = _d[i];

			if (_e[i] > 0)
			{
				D[i][i + 1] = _e[i];
			}
			else if (_e[i] < 0)
			{
				D[i][i - 1] = _e[i];
			}
		}
		return new Matrix<Double>(D);
	}

	private Matrix<Double> ComputeEigentVectors()
	{
		return new Matrix<Double>(_V);
	}

	/** 
	 Symmetric Householder reduction to tridiagonal form.
	*/
	private void SymmetricTridiagonalize()
	{
		//  This is derived from the Algol procedures tred2 by
		//  Bowdler, Martin, Reinsch, and Wilkinson, Handbook for
		//  Auto. Comp., Vol.ii-Linear Algebra, and the corresponding
		//  Fortran subroutine in EISPACK.

		for (int j = 0; j < _n; j++)
		{
			_d[j] = _V[_n - 1][j];
		}

		// Householder reduction to tridiagonal form.

		for (int i = _n - 1; i > 0; i--)
		{
			// Scale to avoid under/overflow.

			double scale = 0.0;
			double h = 0.0;

			for (int k = 0; k < i; k++)
			{
				scale = scale + Math.abs(_d[k]);
			}

			if (scale == 0.0)
			{
				_e[i] = _d[i - 1];

				for (int j = 0; j < i; j++)
				{
					_d[j] = _V[i - 1][j];
					_V[i][j] = 0.0;
					_V[j][i] = 0.0;
				}
			}
			else
			{
				// Generate Householder vector.

				for (int k = 0; k < i; k++)
				{
					_d[k] /= scale;
					h += _d[k] * _d[k];
				}
				double f = _d[i - 1];
				double g = Math.sqrt(h);

				if (f > 0)
				{
					g = -g;
				}
				_e[i] = scale * g;
				h = h - f * g;
				_d[i - 1] = f - g;

				for (int j = 0; j < i; j++)
				{
					_e[j] = 0.0;
				}

				// Apply similarity transformation to remaining columns.

				for (int j = 0; j < i; j++)
				{
					f = _d[j];
					_V[j][i] = f;
					g = _e[j] + _V[j][j] * f;

					for (int k = j + 1; k <= i - 1; k++)
					{
						g += _V[k][j] * _d[k];
						_e[k] += _V[k][j] * f;
					}
					_e[j] = g;
				}
				f = 0.0;

				for (int j = 0; j < i; j++)
				{
					_e[j] /= h;
					f += _e[j] * _d[j];
				}
				double hh = f / (h + h);

				for (int j = 0; j < i; j++)
				{
					_e[j] -= hh * _d[j];
				}

				for (int j = 0; j < i; j++)
				{
					f = _d[j];
					g = _e[j];

					for (int k = j; k <= i - 1; k++)
					{
						_V[k][j] -= (f * _e[k] + g * _d[k]);
					}
					_d[j] = _V[i - 1][j];
					_V[i][j] = 0.0;
				}
			}
			_d[i] = h;
		}

		// Accumulate transformations.

		for (int i = 0; i < _n - 1; i++)
		{
			_V[_n - 1][i] = _V[i][i];
			_V[i][i] = 1.0;
			double h = _d[i + 1];

			if (h != 0.0)
			{
				for (int k = 0; k <= i; k++)
				{
					_d[k] = _V[k][i + 1] / h;
				}

				for (int j = 0; j <= i; j++)
				{
					double g = 0.0;

					for (int k = 0; k <= i; k++)
					{
						g += _V[k][i + 1] * _V[k][j];
					}

					for (int k = 0; k <= i; k++)
					{
						_V[k][j] -= g * _d[k];
					}
				}
			}

			for (int k = 0; k <= i; k++)
			{
				_V[k][i + 1] = 0.0;
			}
		}

		for (int j = 0; j < _n; j++)
		{
			_d[j] = _V[_n - 1][j];
			_V[_n - 1][j] = 0.0;
		}
		_V[_n - 1][_n - 1] = 1.0;
		_e[0] = 0.0;
	}

	/** 
	 Symmetric tridiagonal QL algorithm.
	*/
	private void SymmetricDiagonalize()
	{
		//  This is derived from the Algol procedures tql2, by
		//  Bowdler, Martin, Reinsch, and Wilkinson, Handbook for
		//  Auto. Comp., Vol.ii-Linear Algebra, and the corresponding
		//  Fortran subroutine in EISPACK.

		for (int i = 1; i < _n; i++)
		{
			_e[i - 1] = _e[i];
		}
		_e[_n - 1] = 0.0;

		double f = 0.0;
		double tst1 = 0.0;
		double eps = SpecialFunction.PositiveRelativeAccuracy;

		for (int l = 0; l < _n; l++)
		{
			// Find small subdiagonal element

			tst1 = Math.max(tst1, Math.abs(_d[l]) + Math.abs(_e[l]));
			int m = l;

			while (m < _n)
			{
				if (Math.abs(_e[m]) <= eps * tst1)
				{
					break;
				}
				m++;
			}

			// If m == l, d[l] is an eigenvalue,
			// otherwise, iterate.

			if (m > l)
			{
				int iter = 0;

				do
				{
					iter = iter + 1; // (Could check iteration count here.)

					// Compute implicit shift

					double g = _d[l];
					double p = (_d[l + 1] - g) / (2.0 * _e[l]);
					double r = SpecialFunction.Hypot(p, 1.0);

					if (p < 0)
					{
						r = -r;
					}
					_d[l] = _e[l] / (p + r);
					_d[l + 1] = _e[l] * (p + r);
					double dl1 = _d[l + 1];
					double h = g - _d[l];

					for (int i = l + 2; i < _n; i++)
					{
						_d[i] -= h;
					}
					f = f + h;

					// Implicit QL transformation.

					p = _d[m];
					double c = 1.0;
					double c2 = c;
					double c3 = c;
					double el1 = _e[l + 1];
					double s = 0.0;
					double s2 = 0.0;

					for (int i = m - 1; i >= l; i--)
					{
						c3 = c2;
						c2 = c;
						s2 = s;
						g = c * _e[i];
						h = c * p;
						r = SpecialFunction.Hypot(p, _e[i]);
						_e[i + 1] = s * r;
						s = _e[i] / r;
						c = p / r;
						p = c * _d[i] - s * g;
						_d[i + 1] = h + s * (c * g + s * _d[i]);

						// Accumulate transformation.

						for (int k = 0; k < _n; k++)
						{
							h = _V[k][i + 1];
							_V[k][i + 1] = s * _V[k][i] + c * h;
							_V[k][i] = c * _V[k][i] - s * h;
						}
					}
					p = (-s) * s2 * c3 * el1 * _e[l] / dl1;
					_e[l] = s * p;
					_d[l] = c * p;

					// Check for convergence.
				} while (Math.abs(_e[l]) > eps * tst1);
			}
			_d[l] = _d[l] + f;
			_e[l] = 0.0;
		}

		// Sort eigenvalues and corresponding vectors.

		for (int i = 0; i < _n - 1; i++)
		{
			int k = i;
			double p = _d[i];

			for (int j = i + 1; j < _n; j++)
			{
				if (_d[j] < p)
				{
					k = j;
					p = _d[j];
				}
			}

			if (k != i)
			{
				_d[k] = _d[i];
				_d[i] = p;

				for (int j = 0; j < _n; j++)
				{
					p = _V[j][i];
					_V[j][i] = _V[j][k];
					_V[j][k] = p;
				}
			}
		}
	}

	/** 
	 Nonsymmetric reduction to Hessenberg form.
	*/
	private void NonsymmetricReduceToHessenberg()
	{
		//  This is derived from the Algol procedures orthes and ortran,
		//  by Martin and Wilkinson, Handbook for Auto. Comp.,
		//  Vol.ii-Linear Algebra, and the corresponding
		//  Fortran subroutines in EISPACK.

		int low = 0;
		int high = _n - 1;

		for (int m = low + 1; m <= high - 1; m++)
		{
			// Scale column.

			double scale = 0.0;

			for (int i = m; i <= high; i++)
			{
				scale = scale + Math.abs(H[i][m - 1]);
			}

			if (scale != 0.0)
			{
				// Compute Householder transformation.

				double h = 0.0;

				for (int i = high; i >= m; i--)
				{
					ort[i] = H[i][m - 1] / scale;
					h += ort[i] * ort[i];
				}
				double g = Math.sqrt(h);

				if (ort[m] > 0)
				{
					g = -g;
				}
				h = h - ort[m] * g;
				ort[m] = ort[m] - g;

				// Apply Householder similarity transformation
				// H = (I-u*u'/h)*H*(I-u*u')/h)

				for (int j = m; j < _n; j++)
				{
					double f = 0.0;

					for (int i = high; i >= m; i--)
					{
						f += ort[i] * H[i][j];
					}
					f = f / h;

					for (int i = m; i <= high; i++)
					{
						H[i][j] -= f * ort[i];
					}
				}

				for (int i = 0; i <= high; i++)
				{
					double f = 0.0;

					for (int j = high; j >= m; j--)
					{
						f += ort[j] * H[i][j];
					}
					f = f / h;

					for (int j = m; j <= high; j++)
					{
						H[i][j] -= f * ort[j];
					}
				}
				ort[m] = scale * ort[m];
				H[m][m - 1] = scale * g;
			}
		}

		// Accumulate transformations (Algol's ortran).

		for (int i = 0; i < _n; i++)
		{
			for (int j = 0; j < _n; j++)
			{
				_V[i][j] = (i == j ? 1.0 : 0.0);
			}
		}

		for (int m = high - 1; m >= low + 1; m--)
		{
			if (H[m][m - 1] != 0.0)
			{
				for (int i = m + 1; i <= high; i++)
				{
					ort[i] = H[i][m - 1];
				}

				for (int j = m; j <= high; j++)
				{
					double g = 0.0;

					for (int i = m; i <= high; i++)
					{
						g += ort[i] * _V[i][j];
					}
					// Double division avoids possible underflow
					g = (g / ort[m]) / H[m][m - 1];

					for (int i = m; i <= high; i++)
					{
						_V[i][j] += g * ort[i];
					}
				}
			}
		}
	}

	/** 
	 Nonsymmetric reduction from Hessenberg to real Schur form.
	*/
	private void NonsymmetricReduceHessenberToRealSchur()
	{
		//  This is derived from the Algol procedure hqr2,
		//  by Martin and Wilkinson, Handbook for Auto. Comp.,
		//  Vol.ii-Linear Algebra, and the corresponding
		//  Fortran subroutine in EISPACK.

		// Initialize

		int nn = _n;
		int n = nn - 1;
		int low = 0;
		int high = nn - 1;
		double eps = SpecialFunction.PositiveRelativeAccuracy;
		double exshift = 0.0;
		double p = 0, q = 0, r = 0, s = 0, z = 0, t, w, x, y;

		// Store roots isolated by balanc and compute matrix norm

		double norm = 0.0;

		for (int i = 0; i < nn; i++)
		{
			if (i < low | i > high)
			{
				_d[i] = H[i][i];
				_e[i] = 0.0;
			}

			for (int j = Math.max(i - 1, 0); j < nn; j++)
			{
				norm = norm + Math.abs(H[i][j]);
			}
		}

		// Outer loop over eigenvalue index

		int iter = 0;

		while (n >= low)
		{
			// Look for single small sub-diagonal element

			int l = n;

			while (l > low)
			{
				s = Math.abs(H[l - 1][l - 1]) + Math.abs(H[l][l]);

				if (s == 0.0)
				{
					s = norm;
				}

				if (Math.abs(H[l][l - 1]) < eps * s)
				{
					break;
				}
				l--;
			}

			// Check for convergence
			// One root found

			if (l == n)
			{
				H[n][n] = H[n][n] + exshift;
				_d[n] = H[n][n];
				_e[n] = 0.0;
				n--;
				iter = 0;

				// Two roots found
			}
			else if (l == n - 1)
			{
				w = H[n][n - 1] * H[n - 1][n];
				p = (H[n - 1][n - 1] - H[n][n]) / 2.0;
				q = p * p + w;
				z = Math.sqrt(Math.abs(q));
				H[n][n] = H[n][n] + exshift;
				H[n - 1][n - 1] = H[n - 1][n - 1] + exshift;
				x = H[n][n];

				// Real pair

				if (q >= 0)
				{
					if (p >= 0)
					{
						z = p + z;
					}
					else
					{
						z = p - z;
					}
					_d[n - 1] = x + z;
					_d[n] = _d[n - 1];

					if (z != 0.0)
					{
						_d[n] = x - w / z;
					}
					_e[n - 1] = 0.0;
					_e[n] = 0.0;
					x = H[n][n - 1];
					s = Math.abs(x) + Math.abs(z);
					p = x / s;
					q = z / s;
					r = Math.sqrt(p * p + q * q);
					p = p / r;
					q = q / r;

					// Row modification

					for (int j = n - 1; j < nn; j++)
					{
						z = H[n - 1][j];
						H[n - 1][j] = q * z + p * H[n][j];
						H[n][j] = q * H[n][j] - p * z;
					}

					// Column modification

					for (int i = 0; i <= n; i++)
					{
						z = H[i][n - 1];
						H[i][n - 1] = q * z + p * H[i][n];
						H[i][n] = q * H[i][n] - p * z;
					}

					// Accumulate transformations

					for (int i = low; i <= high; i++)
					{
						z = _V[i][n - 1];
						_V[i][n - 1] = q * z + p * _V[i][n];
						_V[i][n] = q * _V[i][n] - p * z;
					}

					// Complex pair
				}
				else
				{
					_d[n - 1] = x + p;
					_d[n] = x + p;
					_e[n - 1] = z;
					_e[n] = -z;
				}
				n = n - 2;
				iter = 0;

				// No convergence yet
			}
			else
			{
				// Form shift

				x = H[n][n];
				y = 0.0;
				w = 0.0;

				if (l < n)
				{
					y = H[n - 1][n - 1];
					w = H[n][n - 1] * H[n - 1][n];
				}

				// Wilkinson's original ad hoc shift

				if (iter == 10)
				{
					exshift += x;

					for (int i = low; i <= n; i++)
					{
						H[i][i] -= x;
					}
					s = Math.abs(H[n][n - 1]) + Math.abs(H[n - 1][n - 2]);
					x = y = 0.75 * s;
					w = (-0.4375) * s * s;
				}

				// MATLAB's new ad hoc shift

				if (iter == 30)
				{
					s = (y - x) / 2.0;
					s = s * s + w;

					if (s > 0)
					{
						s = Math.sqrt(s);

						if (y < x)
						{
							s = -s;
						}
						s = x - w / ((y - x) / 2.0 + s);

						for (int i = low; i <= n; i++)
						{
							H[i][i] -= s;
						}
						exshift += s;
						x = y = w = 0.964;
					}
				}

				iter = iter + 1; // (Could check iteration count here.)

				// Look for two consecutive small sub-diagonal elements

				int m = n - 2;

				while (m >= l)
				{
					z = H[m][m];
					r = x - z;
					s = y - z;
					p = (r * s - w) / H[m + 1][m] + H[m][m + 1];
					q = H[m + 1][m + 1] - z - r - s;
					r = H[m + 2][m + 1];
					s = Math.abs(p) + Math.abs(q) + Math.abs(r);
					p = p / s;
					q = q / s;
					r = r / s;

					if (m == l)
					{
						break;
					}

					if (Math.abs(H[m][m - 1]) * (Math.abs(q) + Math.abs(r)) < eps * (Math.abs(p) * (Math.abs(H[m - 1][m - 1]) + Math.abs(z) + Math.abs(H[m + 1][m + 1]))))
					{
						break;
					}
					m--;
				}

				for (int i = m + 2; i <= n; i++)
				{
					H[i][i - 2] = 0.0;

					if (i > m + 2)
					{
						H[i][i - 3] = 0.0;
					}
				}

				// Double QR step involving rows l:n and columns m:n

				for (int k = m; k <= n - 1; k++)
				{
					boolean notlast = (k != n - 1);

					if (k != m)
					{
						p = H[k][k - 1];
						q = H[k + 1][k - 1];
						r = (notlast ? H[k + 2][k - 1] : 0.0);
						x = Math.abs(p) + Math.abs(q) + Math.abs(r);

						if (x != 0.0)
						{
							p = p / x;
							q = q / x;
							r = r / x;
						}
					}

					if (x == 0.0)
					{
						break;
					}
					s = Math.sqrt(p * p + q * q + r * r);

					if (p < 0)
					{
						s = -s;
					}

					if (s != 0)
					{
						if (k != m)
						{
							H[k][k - 1] = (-s) * x;
						}
						else if (l != m)
						{
							H[k][k - 1] = -H[k][k - 1];
						}
						p = p + s;
						x = p / s;
						y = q / s;
						z = r / s;
						q = q / p;
						r = r / p;

						// Row modification

						for (int j = k; j < nn; j++)
						{
							p = H[k][j] + q * H[k + 1][j];

							if (notlast)
							{
								p = p + r * H[k + 2][j];
								H[k + 2][j] = H[k + 2][j] - p * z;
							}
							H[k][j] = H[k][j] - p * x;
							H[k + 1][j] = H[k + 1][j] - p * y;
						}

						// Column modification

						for (int i = 0; i <= Math.min(n, k + 3); i++)
						{
							double[] Hi = H[i];
							p = x * Hi[k] + y * Hi[k + 1];

							if (notlast)
							{
								p = p + z * Hi[k + 2];
								Hi[k + 2] = Hi[k + 2] - p * r;
							}
							Hi[k] = Hi[k] - p;
							Hi[k + 1] = Hi[k + 1] - p * q;
						}

						// Accumulate transformations

						for (int i = low; i <= high; i++)
						{
							double[] Vi = _V[i];
							p = x * Vi[k] + y * Vi[k + 1];

							if (notlast)
							{
								p = p + z * Vi[k + 2];
								Vi[k + 2] = Vi[k + 2] - p * r;
							}
							Vi[k] = Vi[k] - p;
							Vi[k + 1] = Vi[k + 1] - p * q;
						}
					} // (s != 0)
				} // k loop
			} // check convergence
		} // while (n >= low)

		// Backsubstitute to find vectors of upper triangular form

		if (norm == 0.0)
		{
			return;
		}

		for (n = nn - 1; n >= 0; n--)
		{
			p = _d[n];
			q = _e[n];

			// Real vector

			if (q == 0)
			{
				int l = n;
				H[n][n] = 1.0;

				for (int i = n - 1; i >= 0; i--)
				{
					w = H[i][i] - p;
					r = 0.0;

					for (int j = l; j <= n; j++)
					{
						r = r + H[i][j] * H[j][n];
					}

					if (_e[i] < 0.0)
					{
						z = w;
						s = r;
					}
					else
					{
						l = i;

						if (_e[i] == 0.0)
						{
							if (w != 0.0)
							{
								H[i][n] = (-r) / w;
							}
							else
							{
								H[i][n] = (-r) / (eps * norm);
							}

							// Solve real equations
						}
						else
						{
							x = H[i][i + 1];
							y = H[i + 1][i];
							q = (_d[i] - p) * (_d[i] - p) + _e[i] * _e[i];
							t = (x * s - z * r) / q;
							H[i][n] = t;

							if (Math.abs(x) > Math.abs(z))
							{
								H[i + 1][n] = (-r - w * t) / x;
							}
							else
							{
								H[i + 1][n] = (-s - y * t) / z;
							}
						}

						// Overflow control

						t = Math.abs(H[i][n]);

						if ((eps * t) * t > 1)
						{
							for (int j = i; j <= n; j++)
							{
								H[j][n] = H[j][n] / t;
							}
						}
					}
				}

				// Complex vector
			}
			else if (q < 0)
			{
				int l = n - 1;

				// Last vector component imaginary so matrix is triangular

				if (Math.abs(H[n][n - 1]) > Math.abs(H[n - 1][n]))
				{
					H[n - 1][n - 1] = q / H[n][n - 1];
					H[n - 1][n] = (-(H[n][n] - p)) / H[n][n - 1];
				}
				else
				{
					cdiv(0.0, -H[n - 1][n], H[n - 1][n - 1] - p, q);
					H[n - 1][n - 1] = cdivr;
					H[n - 1][n] = cdivi;
				}
				H[n][n - 1] = 0.0;
				H[n][n] = 1.0;

				for (int i = n - 2; i >= 0; i--)
				{
					double ra, sa, vr, vi;
					ra = 0.0;
					sa = 0.0;

					for (int j = l; j <= n; j++)
					{
						ra = ra + H[i][j] * H[j][n - 1];
						sa = sa + H[i][j] * H[j][n];
					}
					w = H[i][i] - p;

					if (_e[i] < 0.0)
					{
						z = w;
						r = ra;
						s = sa;
					}
					else
					{
						l = i;

						if (_e[i] == 0)
						{
							cdiv(-ra, -sa, w, q);
							H[i][n - 1] = cdivr;
							H[i][n] = cdivi;
						}
						else
						{
							// Solve complex equations

							x = H[i][i + 1];
							y = H[i + 1][i];
							vr = (_d[i] - p) * (_d[i] - p) + _e[i] * _e[i] - q * q;
							vi = (_d[i] - p) * 2.0 * q;

							if (vr == 0.0 & vi == 0.0)
							{
								vr = eps * norm * (Math.abs(w) + Math.abs(q) + Math.abs(x) + Math.abs(y) + Math.abs(z));
							}
							cdiv(x * r - z * ra + q * sa, x * s - z * sa - q * ra, vr, vi);
							H[i][n - 1] = cdivr;
							H[i][n] = cdivi;

							if (Math.abs(x) > (Math.abs(z) + Math.abs(q)))
							{
								H[i + 1][n - 1] = (-ra - w * H[i][n - 1] + q * H[i][n]) / x;
								H[i + 1][n] = (-sa - w * H[i][n] - q * H[i][n - 1]) / x;
							}
							else
							{
								cdiv(-r - y * H[i][n - 1], -s - y * H[i][n], z, q);
								H[i + 1][n - 1] = cdivr;
								H[i + 1][n] = cdivi;
							}
						}

						// Overflow control

						t = Math.max(Math.abs(H[i][n - 1]), Math.abs(H[i][n]));

						if ((eps * t) * t > 1)
						{
							for (int j = i; j <= n; j++)
							{
								H[j][n - 1] = H[j][n - 1] / t;
								H[j][n] = H[j][n] / t;
							}
						}
					}
				}
			}
		}

		// Vectors of isolated roots

		for (int i = 0; i < nn; i++)
		{
			if (i < low | i > high)
			{
				for (int j = i; j < nn; j++)
				{
					_V[i][j] = H[i][j];
				}
			}
		}

		// Back transformation to get eigenvectors of original matrix

		for (int j = nn - 1; j >= low; j--)
		{
			for (int i = low; i <= high; i++)
			{
				z = 0.0;

				for (int k = low; k <= Math.min(j, high); k++)
				{
					z = z + _V[i][k] * H[k][j];
				}
				_V[i][j] = z;
			}
		}
	}

	/** 
	 Complex scalar division.
	*/
	private void cdiv(double xr, double xi, double yr, double yi)
	{
		// TODO (cdr, 2008-03-11): Refactor

		double r, d;

		if (Math.abs(yr) > Math.abs(yi))
		{
			r = yi / yr;
			d = yr + r * yi;
			cdivr = (xr + r * xi) / d;
			cdivi = (xi - r * xr) / d;
		}
		else
		{
			r = yr / yi;
			d = yi + r * yr;
			cdivr = (r * xr + xi) / d;
			cdivi = (r * xi - xr) / d;
		}
	}
}