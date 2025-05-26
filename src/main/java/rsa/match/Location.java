package rsa.match;

import rsa.quad.HasPoint;

/**
 * A location given by a pair of coordinates (doubles).
 * @param x
 * @param y
 * @author Pedro Batista
 */
public record Location(double x, double y) implements HasPoint { }
