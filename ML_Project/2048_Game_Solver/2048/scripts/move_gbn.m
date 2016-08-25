function move = move_gbn(x, Py, Pxy, parents)
% Predict the label of the given sample 'x'
yi = 17; % Label index
m = size(Pxy, 1);
Y = size(Py, 1);
p = zeros(1, Y);
for y = 1:Y
    x(yi) = y;
    p(y) = Py(y);
    for i = [1:yi-1, yi+1:m]
        p(y) = p(y) * Pxy{i, x(parents(i))}(x(i));
    end
end
[~, move] = max(p);
end