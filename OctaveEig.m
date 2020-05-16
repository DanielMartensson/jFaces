function OctaveEig()
	% Load and perform A*V = B*V*D
	load AB;
	[V, D] = eig(A, B);
	
	% Sort eigenvalues on descended order and change the eigenvectors on the same order as well
	[D, idx] = sort(diag(D), 1, 'descend');
	V = V(:, idx);
	save('VD', 'V', 'D');
	disp('exit') % Exit command for the process builder in java
end