function acc = naive_bayes()
% Train a Naive Bayes classifier and test its accuracy
X_w = read_data('win-train.txt');
% Separate attributes & labels
Y_w = 2*ones(size(X_w, 1), 1); % 2 for Win
X_l = read_data('loss-train.txt');
% Separate attributes & labels
Y_l = ones(size(X_l, 1), 1); % 1 for Loss

[Py, Pxy] = learn([X_w; X_l], [Y_w; Y_l]);
save NB Py Pxy;

X_w = read_data('win-test.txt');
% Separate attributes & labels
Y_w = 2*ones(size(X_w, 1), 1); % 2 for Win
X_l = read_data('win-test.txt');
% Separate attributes & labels
Y_l = ones(size(X_l, 1), 1); % 2 for Win
acc = test(Py, Pxy, [X_w; X_l], [Y_w; Y_l]);
end

function X = read_data(file_name)
% Read data from the given file into X(attributes) & Y(labels)
X = importdata(file_name, ' ');
X = X + 1; % Easy for indexing since 0 indices are not allowed in matlab
end

function [Py, Pxy] = learn(X, Y)
% Learn a NB classifier for the given data (X, Y)
[n, m] = size(X);
L = 2; % Number of labels
V = 12; % Number of values for every feature
Py = histc(Y, 1:L) / n;
Pxy = cell(m, L);
for y = 1:L
    Xy = X(Y == y, :);
    ny = size(Xy, 1);
    for i = 1:m
        Pxy{i, y} = histc(Xy(:, i), 1:V) / ny;
    end
end
end

function acc = test(Py, Pxy, X, Y)
% Test the accuracy of the classifier with probabilities Py & Pxy
n = size(X, 1);
correct = 0;
for i = 1:n
    if predict(Py, Pxy, X(i, :)) == Y(i)
        correct = correct + 1;
    end
end
acc = correct / n;
end

function y = predict(Py, Pxy, x)
% Predict the label of the given sample 'x'
L = size(Py, 1);
m = size(Pxy, 1);
p = zeros(1, L);
for y = 1:L
    p(y) = Py(y);
    for i = 1:m
        p(y) = p(y) * Pxy{i, y}(x(i));
    end
end
[~, y] = max(p);
end