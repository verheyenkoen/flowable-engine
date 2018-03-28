/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.admin.conf;

import java.util.Collections;

import org.flowable.admin.security.AjaxLogoutSuccessHandler;
import org.flowable.admin.security.RemoteIdmAuthenticationProvider;
import org.flowable.app.filter.FlowableCookieFilterRegistrationBean;
import org.flowable.app.properties.FlowableRemoteIdmProperties;
import org.flowable.app.security.ClearFlowableCookieLogoutHandler;
import org.flowable.app.security.CookieConstants;
import org.flowable.app.security.DefaultPrivileges;
import org.flowable.app.service.idm.RemoteIdmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.boot.actuate.info.InfoEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    @Autowired
    private RemoteIdmAuthenticationProvider authenticationProvider;

    @Bean
    public FlowableCookieFilterRegistrationBean flowableCookieFilterRegistrationBean(RemoteIdmService remoteIdmService, FlowableRemoteIdmProperties properties) {
        FlowableCookieFilterRegistrationBean registrationBean = new FlowableCookieFilterRegistrationBean(remoteIdmService, properties);
        registrationBean.setRequiredPrivileges(Collections.singletonList(DefaultPrivileges.ACCESS_ADMIN));
        return registrationBean;
    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {

        // Default auth (database backed)
        auth.authenticationProvider(authenticationProvider);
    }

    @Configuration
    @Order(10)
    public static class FormLoginWebSecurityConfigurerAdapter extends WebSecurityConfigurerAdapter {

        @Autowired
        protected FlowableCookieFilterRegistrationBean flowableCookieFilterRegistrationBean;

        @Autowired
        private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;

        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .addFilterBefore(flowableCookieFilterRegistrationBean.getFilter(), UsernamePasswordAuthenticationFilter.class)
                    .logout()
                    .logoutUrl("/app/logout")
                    .logoutSuccessHandler(ajaxLogoutSuccessHandler)
                    .addLogoutHandler(new ClearFlowableCookieLogoutHandler())
                    .deleteCookies(CookieConstants.COOKIE_NAME)
                    .and()
                    .csrf()
                    .disable()
                    .authorizeRequests()
                    .antMatchers("/app/rest/**").hasAuthority(DefaultPrivileges.ACCESS_ADMIN);
        }
    }

    //
    // Actuator
    //

    @ConditionalOnClass(EndpointRequest.class)
    @Configuration
    @Order(15) // Actuator configuration should kick in after the Form Login so the custom login filter can be applied before
    public static class ActuatorWebSecurityConfigurationAdapter extends WebSecurityConfigurerAdapter {

        protected void configure(HttpSecurity http) throws Exception {

            http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .csrf()
                .disable();

            http
                .authorizeRequests()
                .requestMatchers(EndpointRequest.to(InfoEndpoint.class, HealthEndpoint.class)).authenticated()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasAnyAuthority(DefaultPrivileges.ACCESS_ADMIN)
                .and().httpBasic();
        }
    }
}
