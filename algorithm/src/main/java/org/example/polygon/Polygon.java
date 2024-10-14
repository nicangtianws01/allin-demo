package org.example.polygon;

import java.util.ArrayList;
import java.util.List;

/**
 * 目标点是否在几何图形内
 * 光投射算法
 */
public class Polygon {

    public static class Builder {
        private final List<Point> vertices = new ArrayList<>();
        public Polygon.Builder addVertice(Point point){
            this.vertices.add(point);
            return this;
        }
        public Polygon build(){
            if (vertices.size() < 3) {
                throw new RuntimeException("Polygon must have at least 3 points");
            }
            return new Polygon(this.vertices);
        }
    }

    private List<Point> vertices;

    public Polygon(List<Point> vertices){
        this.vertices = vertices;
    }
    public Polygon() {
        this.vertices = new ArrayList<>();
    }

    public static Builder Builder(){
        return new Polygon.Builder();
    }

    public void setVertices(List<Point> vertices) {
        this.vertices = vertices;
    }
    public List<Point> getVertices() {
        return vertices;
    }

    public boolean contains(List<Point> points){
        for (Point point: points){
            if (!contains(point))
                return false;
        }
        return true;
    }
    public boolean contains(Point point){
        return Polygon.isPointInPolygon(point, this.vertices);
    }

    /**
     * 利用光投射算法计算点是否在多边形内
     *
     * @param point 需要判断的点的坐标
     * @param vertices 多边形按顺时针或逆时针顺序的顶点坐标集合
     * @return 点是否在多边形内
     */
    public static boolean isPointInPolygon(Point point, List<Point> vertices) {
        boolean contains = false;
        for(int i = 0, j = vertices.size() - 1; i < vertices.size(); j = i++) {
            if(((vertices.get(i).y() >= point.y()) != (vertices.get(j).y() >= point.y())) &&
                    (point.x() <= (vertices.get(j).x() - vertices.get(i).x()) * (point.y() - vertices.get(i).y()) / (vertices.get(j).y() - vertices.get(i).y()) + vertices.get(i).x()))
                contains = !contains;
        }
        return contains;
    }

}
