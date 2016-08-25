function acc = bayes_net_move()
% Train a Bayesian Network classifier and test its accuracy
X = read_data('train.txt');

[~, parents] = struct_learn(X); % Bayes Net
%parents = [17 * ones(1, 16), 0]; % Naive Bayes
%parents = [5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 14, 15, 16, 17, 0]; % Custom Bayes Net
Pxy = learn(X, parents);

X = read_data('test.txt');
acc = test(Pxy, parents, X);
Py = Pxy{17, 1};
Pxy = Pxy(1:16, :);
save BN_move Py Pxy parents;
end

function X = read_data(file_name)
% Read data from the given file into X(attributes) & Y(labels)
X = importdata(file_name, ' ');
X = X + 1; % Easy for indexing since 0 indices are not allowed in matlab
end

function Pxy = learn(X, parents)
% Learn the BN probabilities for the given data (X)
[n, m] = size(X);
V = max(X); % Number of values for every feature
Pxy = cell(m, 1);
for i = 1:m
    if parents(i) == 0
        % No parent, so it is the class label
        Pxy{i, 1} = histc(X(:, i), 1:V(i)) / n;
    else
        Y = V(parents(i));
        %Pxy{i, :} = cell(1, Y);
        for y = 1:Y
            Xy = X(X(:, parents(i)) == y, i);
            ny = size(Xy, 1);
            Pxy{i, y} = histc(Xy, 1:V(i)) / ny;
        end
    end
end
end

function acc = test(Pxy, parents, X)
% Test the accuracy of the classifier with probabilities Py & Pxy
n = size(X, 1);
correct = 0;
yi = find(parents == 0); % Label index
for i = 1:n
    if predict(Pxy, parents, X(i, :)) == X(i, yi)
        correct = correct + 1;
    end
end
acc = correct / n;
end

function y = predict(Pxy, parents, x)
% Predict the label of the given sample 'x'
yi = find(parents == 0); % Label index
m = size(Pxy, 1);
Y = size(Pxy{yi, 1}, 1);
p = zeros(1, Y);
for y = 1:Y
    x(yi) = y;
    p(y) = Pxy{yi, 1}(y);
    for i = [1:yi-1, yi+1:m]
        p(y) = p(y) * Pxy{i, x(parents(i))}(x(i));
    end
end
[~, y] = max(p);
end