package roz.graph.services;

import org.mariuszgromada.math.mxparser.Argument;
import org.mariuszgromada.math.mxparser.Expression;
import org.springframework.stereotype.Service;
import roz.graph.dto.MathDtos.*;

import java.util.ArrayList;
import java.util.List;

@Service
public class MathService {

    // ЛИМИТЫ ДЛЯ БЕЗОПАСНОСТИ (Защита от OutOfMemory и зависаний)
    private static final int MAX_2D_POINTS = 10000;
    private static final int MAX_3D_RESOLUTION = 100;
    private static final int MAX_SCAN_STEPS = 5000;
    private static final double EPSILON = 1e-6; // Точность для поиска корней

    // ==========================================
    // 1. POST /api/plot/2d
    // ==========================================
    public Plot2DResponse calculate2D(Plot2DRequest req) {
        validateFormula(req.formula());
        if (req.points() > MAX_2D_POINTS) {
            throw new IllegalArgumentException("Превышен лимит точек. Максимум: " + MAX_2D_POINTS);
        }

        List<Double> xList = new ArrayList<>();
        List<Double> yList = new ArrayList<>();

        Argument x = new Argument("x");
        Expression e = new Expression(req.formula(), x);
        checkSyntax(e);

        double step = (req.xMax() - req.xMin()) / (req.points() - 1);

        for (int i = 0; i < req.points(); i++) {
            double currentX = req.xMin() + i * step;
            x.setArgumentValue(currentX);
            double y = e.calculate();

            // Защита от разрывов (например, 1/x в точке 0)
            if (Double.isNaN(y) || Double.isInfinite(y)) {
                yList.add(null); // Фронтенд должен уметь обрабатывать null (разрыв графика)
            } else {
                yList.add(y);
            }
            xList.add(currentX);
        }
        return new Plot2DResponse(xList, yList);
    }

    // ==========================================
    // 2. POST /api/plot/3d
    // ==========================================
    public Plot3DResponse calculate3D(Plot3DRequest req) {
        validateFormula(req.formula());
        if (req.resolution() > MAX_3D_RESOLUTION) {
            throw new IllegalArgumentException("Превышен лимит разрешения. Максимум: " + MAX_3D_RESOLUTION + "x" + MAX_3D_RESOLUTION);
        }

        List<Double> vertices = new ArrayList<>();
        List<Integer> indices = new ArrayList<>();

        Argument x = new Argument("x");
        Argument y = new Argument("y");
        Expression e = new Expression(req.formula(), x, y);
        checkSyntax(e);

        int res = req.resolution();
        double stepX = (req.xMax() - req.xMin()) / res;
        double stepY = (req.yMax() - req.yMin()) / res;

        // 1. Генерация вершин (vertices)
        for (int i = 0; i <= res; i++) {
            for (int j = 0; j <= res; j++) {
                double curX = req.xMin() + i * stepX;
                double curY = req.yMin() + j * stepY;

                x.setArgumentValue(curX);
                y.setArgumentValue(curY);
                double z = e.calculate();

                if (Double.isNaN(z) || Double.isInfinite(z)) z = 0.0; // Сглаживание разрывов

                vertices.add(curX);
                vertices.add(curY);
                vertices.add(z); // Формат [x, y, z, x, y, z, ...] удобен для Three.js/WebGL
            }
        }

        // 2. Генерация индексов (indices) для триангуляции сетки
        for (int i = 0; i < res; i++) {
            for (int j = 0; j < res; j++) {
                int topLeft = i * (res + 1) + j;
                int topRight = topLeft + 1;
                int bottomLeft = (i + 1) * (res + 1) + j;
                int bottomRight = bottomLeft + 1;

                // Первый треугольник ячейки
                indices.add(topLeft);
                indices.add(bottomLeft);
                indices.add(topRight);

                // Второй треугольник ячейки
                indices.add(topRight);
                indices.add(bottomLeft);
                indices.add(bottomRight);
            }
        }

        return new Plot3DResponse(vertices, indices);
    }

    // ==========================================
    // 3. POST /api/math/derivative
    // ==========================================
    public DerivativeResponse calculateDerivative(DerivativeRequest req) {
        validateFormula(req.formula());

        // Численное дифференцирование (метод центральной разности)
        // f'(x) ≈ (f(x + h) - f(x - h)) / (2h)
        double h = 1e-5;
        Argument x = new Argument("x");
        Expression e = new Expression(req.formula(), x);
        checkSyntax(e);

        x.setArgumentValue(req.x() + h);
        double y1 = e.calculate();

        x.setArgumentValue(req.x() - h);
        double y2 = e.calculate();

        double derivative = (y1 - y2) / (2 * h);
        return new DerivativeResponse(req.x(), derivative);
    }

    // ==========================================
    // 4. POST /api/math/integral
    // ==========================================
    public IntegralResponse calculateIntegral(IntegralRequest req) {
        validateFormula(req.formula());
        if (req.a() >= req.b()) {
            throw new IllegalArgumentException("Нижний предел (a) должен быть меньше верхнего (b)");
        }

        // Численное интегрирование методом Симпсона
        int n = 1000; // Количество разбиений (должно быть четным)
        double h = (req.b() - req.a()) / n;

        Argument x = new Argument("x");
        Expression e = new Expression(req.formula(), x);
        checkSyntax(e);

        double sum = 0.0;
        for (int i = 0; i <= n; i++) {
            double curX = req.a() + i * h;
            x.setArgumentValue(curX);
            double y = e.calculate();
            if (Double.isNaN(y) || Double.isInfinite(y)) y = 0;

            if (i == 0 || i == n) {
                sum += y;
            } else if (i % 2 == 0) {
                sum += 2 * y;
            } else {
                sum += 4 * y;
            }
        }
        double area = (h / 3) * sum;
        return new IntegralResponse(area);
    }

    // ==========================================
    // 5. POST /api/math/roots
    // ==========================================
    public RootsResponse findRootsAndExtrema(RootsRequest req) {
        validateFormula(req.formula());
        double step = Math.abs(req.step());
        if (step <= 0) step = 0.1;

        int stepsCount = (int) Math.ceil((req.xMax() - req.xMin()) / step);
        if (stepsCount > MAX_SCAN_STEPS) {
            throw new IllegalArgumentException("Слишком большой диапазон или слишком маленький шаг. Максимум итераций: " + MAX_SCAN_STEPS);
        }

        List<Point> roots = new ArrayList<>();
        List<Point> extrema = new ArrayList<>();

        Argument x = new Argument("x");
        Expression e = new Expression(req.formula(), x);
        checkSyntax(e);

        double prevX = req.xMin();
        x.setArgumentValue(prevX);
        double prevY = e.calculate();

        for (int i = 1; i <= stepsCount; i++) {
            double curX = req.xMin() + i * step;
            x.setArgumentValue(curX);
            double curY = e.calculate();

            // 1. Поиск корней (пересечение нуля)
            if (!Double.isNaN(prevY) && !Double.isNaN(curY)) {
                if (prevY * curY < 0) { // Знак сменился
                    double rootX = bisection(e, x, prevX, curX, prevY);
                    roots.add(new Point(rootX, 0.0));
                } else if (Math.abs(curY) < EPSILON) { // Точное попадание в 0
                    roots.add(new Point(curX, 0.0));
                }
            }

            // 2. Поиск экстремумов (смена знака производной)
            // Приближенная производная в текущей точке
            double h = 1e-4;
            x.setArgumentValue(curX + h);
            double yPlus = e.calculate();
            x.setArgumentValue(curX - h);
            double yMinus = e.calculate();
            double deriv = (yPlus - yMinus) / (2 * h);

            x.setArgumentValue(prevX + h);
            double prevYPlus = e.calculate();
            x.setArgumentValue(prevX - h);
            double prevYMinus = e.calculate();
            double prevDeriv = (prevYPlus - prevYMinus) / (2 * h);

            if (prevDeriv * deriv < 0) {
                // Производная сменила знак -> локальный экстремум
                double extremaX = bisectionDerivative(e, x, prevX, curX);
                x.setArgumentValue(extremaX);
                extrema.add(new Point(extremaX, e.calculate()));
            }

            prevX = curX;
            prevY = curY;
        }

        return new RootsResponse(roots, extrema);
    }

    // --- Вспомогательные методы ---

    private void validateFormula(String formula) {
        if (formula == null || formula.isBlank() || formula.length() > 100) {
            throw new IllegalArgumentException("Некорректная формула");
        }
        // Дополнительная защита: запрещаем опасные системные вызовы (на всякий случай)
        String lower = formula.toLowerCase();
        if (lower.contains("runtime") || lower.contains("system") || lower.contains("exec") || lower.contains("import")) {
            throw new SecurityException("Запрещенная формула");
        }
    }

    private void checkSyntax(Expression e) {
        if (!e.checkSyntax()) {
            throw new IllegalArgumentException("Синтаксическая ошибка в формуле: " + e.getErrorMessage());
        }
    }

    // Метод бисекции для уточнения корня
    private double bisection(Expression e, Argument x, double a, double b, double yA) {
        double mid = 0;
        for (int i = 0; i < 50; i++) { // 50 итераций достаточно для точности 1e-15
            mid = (a + b) / 2;
            x.setArgumentValue(mid);
            double yMid = e.calculate();
            if (Math.abs(yMid) < EPSILON || (b - a) / 2 < EPSILON) break;

            if (yA * yMid < 0) {
                b = mid;
            } else {
                a = mid;
                yA = yMid;
            }
        }
        return mid;
    }

    // Метод бисекции для поиска экстремума (корень производной)
    private double bisectionDerivative(Expression e, Argument x, double a, double b) {
        double h = 1e-5;
        double mid = 0;
        for (int i = 0; i < 50; i++) {
            mid = (a + b) / 2;

            x.setArgumentValue(mid - h);
            double y1 = e.calculate();
            x.setArgumentValue(mid + h);
            double y2 = e.calculate();
            double derivMid = (y2 - y1) / (2 * h);

            x.setArgumentValue(a - h);
            double ya1 = e.calculate();
            x.setArgumentValue(a + h);
            double ya2 = e.calculate();
            double derivA = (ya2 - ya1) / (2 * h);

            if (Math.abs(derivMid) < EPSILON || (b - a) / 2 < EPSILON) break;

            if (derivA * derivMid < 0) {
                b = mid;
            } else {
                a = mid;
            }
        }
        return mid;
    }
}