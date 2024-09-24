package com.techforcebuddybl.jwt;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.techforcebuddybl.services.CustomUserDetailsService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/*
 * This is the class which implement the OncePerRequestFilter
 * to check every request is authorized or not.
 */

@Component
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUtil jwtUtil;

	@Autowired
	private CustomUserDetailsService userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		// get the header from the request
		String header = request.getHeader("Authorization");
		String token = null;
		String userName = null;

		// Check if the header start with "Bearer"
		if (header != null && header.startsWith("Bearer ")) {
			token = header.substring(7); // Extract token
			log.info("Token :"+token);
			userName = jwtUtil.extractUsername(token); // Extract the username
			log.info("UNM :"+userName);
		}

		// If the token is valid and no authentication is set in the context
		if (userName != null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
			// Validate token and set authentication
			if (jwtUtil.validateToken(token, userDetails)) {
				try {
					UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails,
							null,userDetails.getAuthorities());
					authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authToken);
					response.setHeader("Authorization", "Bearer " + token);
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}

			} else {
				response.sendError(401, "Token not valid");
			}
		}

		// Continue the filter chain
		filterChain.doFilter(request, response);
	}

}
